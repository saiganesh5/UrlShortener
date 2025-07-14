import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ShortenPage from './pages/ShortenPage';
import StatsPage from './pages/StatsPage';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';

function App() {
    return (
        <Router>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" sx={{ flexGrow: 1 }}>
                        🔗 URL Shortener
                    </Typography>
                    <Button color="inherit" component={Link} to="/">Shorten</Button>
                    <Button color="inherit" component={Link} to="/stats">Stats</Button>
                </Toolbar>
            </AppBar>
            <Routes>
                <Route path="/" element={<ShortenPage />} />
                <Route path="/stats" element={<StatsPage />} />
            </Routes>
        </Router>
    );
}

export default App;

