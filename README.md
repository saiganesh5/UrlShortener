```markdown
#  URL Shortener

A full-stack **URL Shortener application** built with **Spring Boot (Java)** for the backend and **React.js** for the frontend.

It allows users to shorten long URLs, track the number of clicks, and view basic analytics like browser and location data.

---

## Project Structure

```

UrlShortener/
├── frontend/              # React frontend
├── src/                   # Spring Boot backend
├── pom.xml                # Maven config for backend
├── README.md              # You're here!

```

---

## Backend (Spring Boot)

### 📌 Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- MySQL
- Geo IP Resolution
- Maven

### Features
- Generate a short code for any valid long URL.
- Redirect from short URL to the original one.
- Logs browser, IP, and location info on each click.

### Backend Directory Structure
```

src/main/java/org/ganesh/urlshortener/
├── controller/           # API endpoints
├── service/              # Business logic
├── model/                # Entities (Url, ClickLog)
├── repository/           # JPA Repositories
├── util/                 # GeoResolver, ShortCodeGenerator
└── UrlShortenerApplication.java

````

### How to Run
1. Configure your database in `application.properties`
2. Build & run:
```bash
mvn spring-boot:run
````

3. Access API at `http://localhost:8080`

---

## Frontend (React)

### Tech Stack

* React.js (Vite or CRA)
* Axios for API calls
* Bootstrap / Tailwind (Optional for styling)

###  Features

* Input long URL and receive short version.
* Display shortened URL and copy to clipboard.
* View analytics (click count, device, location).
* Separate pages for URL shortening and stats.

### Frontend Directory Structure

```
frontend/
├── src/
│   ├── components/       # Reusable components (UrlForm, UrlStats)
│   ├── pages/            # ShortenPage, StatsPage
│   ├── api.js            # Axios API configuration
│   └── App.js
```

### How to Run

```bash
cd frontend
npm install
npm start
```

Then open `http://localhost:3000` in your browser.

---

## Backend ↔ Frontend Integration

Ensure that the React frontend sends requests to the backend. You can do this by updating the `baseURL` in `src/api.js`:

```js
// frontend/src/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

export default api;
```

---
