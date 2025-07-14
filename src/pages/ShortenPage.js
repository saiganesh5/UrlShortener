import React, { useState } from 'react';
import { TextField, Button, Grid, Typography, Card, CardContent, Snackbar } from '@mui/material';
import API from '../api';

function ShortenPage() {
    const [inputs, setInputs] = useState([{ url: '', validity: '', shortcode: '' }]);
    const [results, setResults] = useState([]);
    const [error, setError] = useState('');

    const handleChange = (index, field, value) => {
        const updated = [...inputs];
        updated[index][field] = value;
        setInputs(updated);
    };

    const addInputField = () => {
        if (inputs.length < 5) {
            setInputs([...inputs, { url: '', validity: '', shortcode: '' }]);
        }
    };

    const shortenUrls = async () => {
        setResults([]);
        for (const input of inputs) {
            if (!input.url.trim()) {
                setError("URL is required");
                return;
            }
            try {
                const res = await API.post('/shorturls', {
                    url: input.url,
                    validity: input.validity ? parseInt(input.validity) : undefined,
                    shortcode: input.shortcode || undefined,
                });
                setResults(prev => [...prev, res.data]);
            } catch (err) {
                setError(err.response?.data?.message || "Error shortening URL");
            }
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <Typography variant="h4" gutterBottom>Shorten URLs</Typography>
            <Grid container spacing={2}>
                {inputs.map((input, idx) => (
                    <Grid item xs={12} md={6} key={idx}>
                        <Card variant="outlined">
                            <CardContent>
                                <Typography variant="h6">URL #{idx + 1}</Typography>
                                <TextField fullWidth margin="dense" label="Long URL" value={input.url} onChange={(e) => handleChange(idx, 'url', e.target.value)} />
                                <TextField fullWidth margin="dense" label="Validity (mins)
                                (default-30mins)" value={input.validity} onChange={(e) => handleChange(idx, 'validity', e.target.value)} />
                                <TextField fullWidth margin="dense" label="Custom Shortcode (optional)" value={input.shortcode} onChange={(e) => handleChange(idx, 'shortcode', e.target.value)} />
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <Button variant="contained" sx={{ mt: 2, mr: 2 }} onClick={addInputField}>➕ Add Another</Button>
            <Button variant="contained" sx={{ mt: 2 }} onClick={shortenUrls}>🔗 Shorten All</Button>

            <div style={{ marginTop: 30 }}>
                {results.map((res, idx) => (
                    <Card key={idx} sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography>✅ <strong>Short URL:</strong> <a href={res.shortLink} target="_blank" rel="noreferrer">{res.shortLink}</a></Typography>
                            <Typography>⏰ <strong>Expires at:</strong> {res.expiry}</Typography>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <Snackbar open={!!error} autoHideDuration={4000} onClose={() => setError('')} message={error} />
        </div>
    );
}

export default ShortenPage;
