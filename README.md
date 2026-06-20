# NyayaAI ⚖️

**NyayaAI** is a premium legal-tech mobile application designed to provide instant legal intelligence at your fingertips. Built with Android (Kotlin & Jetpack Compose), it offers a seamless AI-driven legal assistance experience tailored for the modern era.

## 🚀 Key Features

- **Legal AI Assistant**: Get instant answers to your legal queries using a specialized legal LLM.
- **Lawyer Consultation System**: Find legal professionals, send consultation requests, and track status in real-time.
- **Instant Real-Time Chat**: Direct communication between clients and lawyers with instant messaging and live updates.
- **Real-Time Notifications**: Get notified instantly when a lawyer accepts your request or when you receive a new message.
- **Smart Formatting**: Support for bold text and Markdown formatting in AI responses for better readability.
- **Persistent Conversations**: Your chat history and legal requests are saved securely.
- **Premium UI/UX**: Modern, interactive design with specialized dashboards for both Lawyers and Clients.
- **Emergency Contacts**: One-tap access to essential emergency services (Police, Women's Helpline, Ambulance, Child Help).

## 📱 Screenshots

<div align="center">
  <img src="screenshots/img_1.png" width="300" alt="Chat Screen"/>
  <img src="screenshots/img.png" width="300" alt="Home Screen"/>
</div>

## 🛠️ Technical Stack

- **Framework**: [Jetpack Compose](https://developer.android.com/compose)
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Backend/Database**: [Firebase](https://firebase.google.com/) (Auth, Firestore, Cloud Messaging)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Data Persistence**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## ⚙️ Backend Integration

The app connects to a Python/Flask-based backend hosted on Hugging Face Spaces at:
- **Base URL**: `https://kazuaki83358-nyaya-backend.hf.space/`
- **Chat Endpoint**: `https://kazuaki83358-nyaya-backend.hf.space/chat`
- **Request Format**: 
  ```json
  {
    "message": "Your legal question here"
  }
  ```
- **Response Format**: 
  ```json
  {
    "response": "The AI's legal guidance"
  }
  ```

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

*NyayaAI - Empowering Justice through Technology.*
