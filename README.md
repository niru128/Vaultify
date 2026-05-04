# 🔐 Vaultify – Secure Cloud File Storage System

Vaultify is a full-stack cloud storage application that allows users to securely upload, manage, and organize files with folder support and password protection.


## 🧠 Features

### 🔐 Authentication

* User Registration & Login
* JWT-based authentication
* Secure API access

### 📁 File Management

* Upload files to cloud (Supabase)
* Download files
* Delete files
* View file metadata (size, date)

### 📂 Folder System

* Create folders
* Upload files inside folders
* Filter files by folder
* Delete folders (cascade delete)

### 🔍 Search & Storage

* Search files instantly
* Storage usage tracking

### 🔒 File-Level Security (🔥 Highlight Feature)

* Enable password protection per file
* Account password verification before enabling protection
* Secure download/delete with password validation
* Encrypted password storage (BCrypt)

---

## 🛠️ Tech Stack

### Frontend

* React.js
* Tailwind CSS
* Axios
* React Hot Toast

### Backend

* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate / JPA

### Database & Storage

* Supabase (PostgreSQL + Storage)

### Deployment

* Backend → Render
* Frontend → Vercel

---

## ⚙️ Environment Variables

### Backend (.env / Render)

```
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key
SUPABASE_BUCKET=vaultify-files
JWT_SECRET=your_secret_key
```

---

## 🧪 API Endpoints

### Auth

```
POST /api/auth/register
POST /api/auth/login
```

### Files

```
POST   /api/files/upload
GET    /api/files/my-files
GET    /api/files/{id}/download-link
DELETE /api/files/{id}
POST   /api/files/{id}/enable-protection
```

### Folders

```
POST   /api/folders
GET    /api/folders
DELETE /api/folders/{id}
```

---

## 🔐 Security Implementation

* Passwords hashed using BCrypt
* JWT for authentication
* File-level password protection
* Authorization checks for every file operation

---

## 🧩 Project Structure

```
vaultify/
│
├── frontend/        # React App
│   ├── components/
│   ├── pages/
│   └── services/
│
├── backend/         # Spring Boot App
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
```

##Screenshots<img width="1901" height="911" alt="Screenshot 2026-05-04 193900" src="https://github.com/user-attachments/assets/e13a299e-30c7-48fb-b36f-23e53106b7f4" />
<img width="1919" height="914" alt="Screenshot 2026-05-04 193850" src="https://github.com/user-attachments/assets/c8f44664-4acd-4d4a-b2bb-4b28a77c7542" />
<img width="1919" height="911" alt="Screenshot 2026-05-04 193833" src="https://github.com/user-attachments/assets/87bd38ed-2445-490a-90e0-5e9f578af7dd" />
<img width="1905" height="913" alt="Screenshot 2026-05-04 193815" src="https://github.com/user-attachments/assets/2291c2e1-00c8-4bbb-8d72-71734265a901" />


---

## 🏆 Key Highlights

* Full-stack production-ready app
* Secure file access system
* Cloud storage integration
* Clean UI with modern UX
* Scalable backend architecture

---

## 📌 Future Improvements

* File preview (PDF/Image)
* Drag & drop upload
* Rename files/folders
* Role-based access control

---

## 👨‍💻 Author

**Niranjan C B**


---

## ⭐ If you like this project

Give it a ⭐ on GitHub and share it!

---
