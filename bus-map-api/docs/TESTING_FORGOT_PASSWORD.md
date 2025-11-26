# Testing Forgot Password Endpoint

## Postman Collection Updated ✅

A new request has been added to the **BusMap API** Postman collection:

**Auth → POST /forgot-password**

## How to Use

### 1. Import/Reload the Collection

If you already have the collection imported in Postman:
- Right-click on "BusMap API" collection
- Click "Reload from file"
- Or re-import: `postman/BusMap.postman_collection.json`

### 2. Configure the Request

The request is pre-configured with:
- **Method**: POST
- **URL**: `{{API_HOST}}:{{API_PORT}}/{{API_URL}}/forgot-password`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "email": "user@example.com"
  }
  ```

### 3. Update the Email

Replace `user@example.com` with an actual email from your database:

```json
{
  "email": "test@example.com"
}
```

### 4. Prerequisites

Before testing, ensure:

1. **User has an email in the database**:
   ```sql
   UPDATE usuarios SET email = 'test@example.com' WHERE username = 'testuser';
   ```

2. **Gmail credentials are configured** in `secrets.properties`:
   ```properties
   gmail.username=your-email@gmail.com
   gmail.password=your-app-password
   ```

3. **Application is running**:
   - Start the Bus Map API application
   - Verify it's running on the configured port (default: 8080)

### 5. Send the Request

Click **Send** in Postman

### Expected Responses

#### Success Response (200 OK):
```json
"Password reset successful. Check your email for the new password."
```

#### Email Not Found (200 OK - Security Response):
```json
"If the email exists in our system, a password reset link will be sent"
```

#### Invalid Request (400 Bad Request):
```json
"Email is required"
```

#### Server Error (500 Internal Server Error):
```json
"Password was reset but failed to send email. Please contact support."
```
or
```json
"Failed to update password"
```

## What Happens When You Send the Request

1. **Validates** the email field is provided
2. **Searches** for a user with that email in the database
3. **Generates** a random 12-character password
4. **Updates** the user's password (hashed) in the database
5. **Sends** an email to the user with:
   - Subject: "Password Reset - Bus Map API"
   - The username
   - The new temporary password
   - Instructions to change it immediately

## Testing the Flow

### Complete Test Scenario:

1. **Setup a test user** with an email:
   ```sql
   UPDATE usuarios SET email = 'your-real-email@gmail.com' WHERE username = 'testuser';
   ```

2. **Send the forgot-password request**:
   ```json
   POST /forgot-password
   {
     "email": "your-real-email@gmail.com"
   }
   ```

3. **Check your email** for the new password

4. **Login with the new password**:
   ```json
   POST /login
   {
     "username": "testuser",
     "password": "new-password-from-email"
   }
   ```

5. **Success!** You should receive a JWT token

## Troubleshooting

### Email Not Sending?

Check the application logs for errors:
- Gmail authentication issues
- Network connectivity
- Invalid App Password

### User Not Found?

Verify the user exists and has an email:
```sql
SELECT usuario_id, username, email FROM usuarios WHERE email = 'test@example.com';
```

### Email Goes to Spam?

- Check your Gmail spam folder
- Gmail might flag emails from new App Passwords initially
- Mark as "Not Spam" to whitelist future emails

## Security Notes

- The endpoint returns a generic message whether the email exists or not (prevents email enumeration)
- The new password is sent ONLY to the email address in the database
- Passwords are hashed before storage
- The endpoint does NOT require authentication (it's for forgotten passwords!)
