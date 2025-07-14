import React, { useState } from 'react';
import { TextField, Button, Typography, Snackbar, Card, CardContent } from '@mui/material';
import API from '../api';
import { Line } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    LineElement,
    CategoryScale,
    LinearScale,
    PointElement
} from 'chart.js';

ChartJS.register(LineElement, CategoryScale, LinearScale, PointElement);

function StatsPage() {
    const [code, setCode] = useState('');
    const [stats, setStats] = useState(null);
    const [error, setError] = useState('');

    const fetchStats = async () => {
        try {
            const res = await API.get(`/shorturls/${code}`);
            setStats(res.data);
            setError('');
        } catch (err) {
            setStats(null);
            setError(err.response?.data?.message || 'Error fetching stats');
        }
    };

    const chartData = stats?.clickData?.length > 0 ? {
        labels: stats.clickData.map((log, index) => `#${index + 1}`),
        datasets: [
            {
                label: 'Click Timestamps',
                data: stats.clickData.map(log => new Date(log.timestamp).getTime()),
                backgroundColor: 'rgba(75,192,192,0.2)',
                borderColor: 'rgba(75,192,192,1)',
                borderWidth: 2,
                pointRadius: 3
            }
        ]
    } : null;

    const chartOptions = {
        scales: {
            y: {
                ticks: {
                    callback: function (value) {
                        const date = new Date(value);
                        return date.toLocaleString();
                    }
                }
            }
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <Typography variant="h4" gutterBottom>Short URL Statistics</Typography>
            <TextField
                label="Enter Shortcode"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                fullWidth
                sx={{ mt: 2, mb: 2 }}
            />
            <Button variant="contained" onClick={fetchStats}>Get Stats</Button>

            {stats && (
                <Card sx={{ mt: 4 }}>
                    <CardContent>
                        <Typography>🔗 Original URL: {stats.originalUrl}</Typography>
                        <Typography>📅 Created At: {stats.createdAt}</Typography>
                        <Typography>⏰ Expires At: {stats.expiry}</Typography>
                        <Typography>📈 Total Clicks: {stats.clicks}</Typography>

                        {chartData && (
                            <>
                                <Typography variant="h6" sx={{ mt: 3 }}>Click Timeline</Typography>
                                <Line data={chartData} options={chartOptions} />
                            </>
                        )}

                        {stats.clickData?.length > 0 && (
                            <>
                                <Typography sx={{ mt: 3 }}>🧾 Click Logs:</Typography>
                                <ul>
                                    {stats.clickData.map((c, idx) => (
                                        <li key={idx}>
                                            {c.timestamp} — {c.referrer} — {c.location}
                                        </li>
                                    ))}
                                </ul>
                            </>
                        )}
                    </CardContent>
                </Card>
            )}

            <Snackbar open={!!error} autoHideDuration={4000} onClose={() => setError('')} message={`❌ ${error}`} />
        </div>
    );
}

export default StatsPage;
