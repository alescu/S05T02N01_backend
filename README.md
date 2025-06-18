# Pet Academy: Virtual Pet Application
Pet Academy is a virtual pet application that allows users to care for and nurture their own digital companions. It offers a comprehensive set of features to enhance the user experience:

### In this application you can:
1.  **Register:**
	* New users can sign up by creating a username and password.
2.  **Login:** 
    * Existing users can log in with their credentials to obtain a JWT token, granting them access to the application.
3.  **Create:** 
    * Users can bring new virtual pets to life, choosing from a diverse selection of creatures such as dragons, unicorns, and
4.  **Read:** 
    * The application displays all existing virtual pets within a vibrant and colorful virtual environment. Users can interact with their pets and monitor their mood, energy levels, and needs.
5.  **Update:** 
    * Users can nurture and personalize their virtual pets by feeding them, playing with them, purchasing fun accessories, and even pets.
6.  **Delete:** 
    * Users have the option to remove virtual pets they no longer wish to care for. Upon confirmation, the pet disappears from their virtual environment, but they can always create a new one.
7.  **ROLES:** 
    * User (ROLE_USER): Users with this role can only access, view, update, and delete their own virtual pets.
    * Admin (ROLE_ADMIN): Administrator have full access to all virtual pets in the system, allowing them to view, update, and block any user  regardless of its owner.
    * Admin (ROLE_SUB_ADMIN): The ROLE_ADMIN Administrator can convert users to  ROLE_SUB_ADMIN, and reverse. they have full access to all virtual pets in the system, allowing them to view, update, and block any user regardless of its owner.

## Installation

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Internationalization](#internationalization)
- [Contributing](#contributing)
- [Testing](#testing)
- [License](#license)
- [Contact](#contact)

## Overview

Pet Academy is a comprehensive virtual pet management system that combines the nostalgia of classic Tamagotchi-style games with modern web technologies. Users can register, create multiple pets, and take care of them by managing their hunger, happiness, energy, health, and hygiene levels. The application features a beautiful, responsive interface with multiple themes and full internationalization support.

## Features

### 🎮 Core Gameplay
- **Pet Creation**: Create custom pets with different types (cats, dogs, etc.)
- **Pet Care**: Feed, play, clean, and heal your pets
- **Status Management**: Monitor hunger, happiness, energy, health, and hygiene
- **Customization**: Personalize pets with objects and backgrounds

### 🎨 User Experience
- **Multi-theme Support**: Choose from various visual themes
- **Responsive Design**: Optimized for desktop and mobile devices
- **Internationalization**: Available in Catalan, Spanish, English, and French
- **Real-time Updates**: Live pet status monitoring

### 👥 User Management
- **Authentication**: Secure JWT-based login system
- **Role-based Access**: User and staff/admin roles
- **Profile Management**: User registration and profile customization
- **Password Recovery**: Secure password renewal system

### 🛡️ Administration
- **User Management**: Block/unblock users, change roles
- **Pet Oversight**: View and manage all pets in the system
- **Security Controls**: Mark users for password renewal

## Tech Stack

### Frontend
- **Framework**: Next.js 14 with App Router
- **Language**: TypeScript
- **Styling**: Tailwind CSS + shadcn/ui components
- **State Management**: React Context + Custom Hooks
- **Authentication**: JWT tokens with secure storage

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java
- **Security**: Spring Security with JWT
- **Database**: MongoDB / PostgreSQL
- **API**: RESTful API with OpenAPI 3.0 documentation

## Requirements

### Frontend
- Node.js 18.0 or higher
- npm 9.0 or higher (or yarn/pnpm equivalent)

### Backend
- Java 21 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher
- PostgreSQL

### Development Tools
- Git
- Modern web browser
- Code editor (IntelliJ recommended)

## Installation

### 1. Clone the backend Repository
\`\`\`bash
git clone https://github.com/alescu/S05T02N01_backend.git
cd pet-academy
\`\`\`

### 2. Frontend Setup
    Read the README.md documentation in https://github.com/alescu/S05T02N01_frontend.git


# Configure environment variables

# NEXT_PUBLIC_API_URL=http://localhost:8081
\`\`\`

### 3. Backend Setup
\`\`\`bash
# Navigate to backend directory
cd backend

# Install dependencies
mvn clean install

# Configure application properties
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Update database connection and JWT secret
\`\`\`

### 4. Database Setup
\`\`\`bash
# Start MongoDB (Docker or application)
docker run -d -p 27017:27017 --name pet-academy-db mongo:latest (Example)

# Or install MongoDB locally following official documentation
\`\`\`
# Start PostgresSQL (Docker or application)
In 33061 port and then use the script sql to create the database and tables.


### Starting the Application

1. **Start the Backend**:
\`\`\`bash
cd backend
mvn spring-boot:run
\`\`\`
The API will be available at `http://localhost:8081`

2. **Start the Frontend**:
\`\`\`bash
cd frontend
npm run dev
\`\`\`
The application will be available at `http://localhost:3000`

3. **read the Backend API information**:
The API will be available at `http://localhost:8081/swagger-ui/index.htm`


### Basic Workflow

1. **Login**: Access your account at `/login`
2. **Register**: Create a user new account at `/register`
3. **Create Pet**: Add your first virtual pet from the dashboard
4. **Play with your Pet**: Use the pet detail page to feed, play, and care for your pet
4. **Delete your Pet**: You can delete your pets
5. **Monitorize Status**: Keep track of your pet's health and happiness levels
6. **Monitorize users**: Keep track of your user's
7. **Create admin users**: You can create news admin user's
8. **Create users from Admin user**: You can create news user from admin user.

### Admin Features

Staff users can access additional features:
- User management at `/admin`
- System-wide pet monitoring
- User role management and security controls

## API Documentation

The backend provides a comprehensive REST API with the following main endpoints:

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/registerStaff` - Staff registration
- `POST /auth/userRenewPassword` - Password renewal

### User Management
- `GET /user/{userName}` - Get user details
- `DELETE /user/{userName}` - Delete user
- `GET /user/{userName}/pet` - Get user's pets
- `POST /user/{userName}/pet` - Create new pet

### Pet Management
- `GET /user/{userName}/pet/{petName}` - Get specific pet
- `PUT /user/{userName}/pet/{petName}` - Update pet status
- `DELETE /user/{userName}/pet/{petName}` - Delete pet

### Administration
- `GET /admin/users` - List all users
- `GET /admin/pets` - List all pets
- `PUT /admin/{adminUser}/blockUser` - Block user
- `PUT /admin/{adminUser}/unblockUser` - Unblock user
- `PUT /admin/{adminUser}/changeRole` - Change user role

All endpoints require JWT authentication except for registration and login.

## Internationalization

Pet Academy supports four languages:

- **Catalan** (ca) - Default language
- **Spanish** (es)
- **English** (en)
- **French** (fr)

The language preference is automatically saved and persists across sessions. Users can switch languages using the floating language selector available on all pages.

## Contributing

We welcome contributions to Pet Academy! Here's how you can help:

### Reporting Issues
- Use GitHub Issues to report bugs
- Include detailed reproduction steps
- Provide browser/system information

### Suggesting Features
- Open a GitHub Issue with the "enhancement" label
- Describe the feature and its benefits
- Include mockups or examples if applicable

### Pull Requests
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and test thoroughly
4. Follow the existing code style and conventions
5. Write or update tests as needed
6. Commit with clear, descriptive messages
7. Push to your fork and submit a pull request

### Code Style
- Frontend: Follow TypeScript and React best practices
- Backend: Follow Java and Spring Boot conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure responsive design for UI changes

## Testing

### Frontend Testing
\`\`\`bash
cd frontend
npm run test        # Run unit tests
npm run test:e2e    # Run end-to-end tests
npm run lint        # Check code style
\`\`\`

### Backend Testing
\`\`\`bash
cd backend
mvn test           # Run unit tests
mvn verify         # Run integration tests
\`\`\`

### Manual Testing
1. Test user registration and login flows
2. Verify pet creation and management
3. Check responsive design on different devices
4. Test internationalization with different languages
5. Validate admin functionality (if applicable)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

\`\`\`
MIT License

Copyright (c) 2024 Pet Academy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
\`\`\`

## Contact

- **Project Maintainer**: [Your Name](mailto:your.email@example.com)
- **GitHub**: [https://github.com/yourusername/pet-academy](https://github.com/yourusername/pet-academy)
- **Issues**: [https://github.com/yourusername/pet-academy/issues](https://github.com/yourusername/pet-academy/issues)

## Acknowledgments

- **shadcn/ui** for the beautiful component library
- **Tailwind CSS** for the utility-first CSS framework
- **Spring Boot** for the robust backend framework
- **Next.js** for the powerful React framework
- **MongoDB** for the flexible database solution

---

**Pet Academy** - Where digital pets come to life! 🐾✨
