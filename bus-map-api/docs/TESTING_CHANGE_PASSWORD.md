# Testing Change Password Endpoint

## Postman Collection Updated ✅

A new request has been added to the **BusMap API** Postman collection:

**Auth → POST /change-password**

## How to Use

### 1. Import/Reload the Collection

If you already have the collection imported in Postman:
- Right-click on "BusMap API" collection
- Click "Reload from file"
- Or re-import: `postman/BusMap.postman_collection.json`

### 2. Configure the Request

The request is pre-configured with:
- **Method**: POST
- **URL**: `{{API_HOST}}:{{API_PORT}}/{{API_URL}}/change-password`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt}}` (automatically set from login)
- **Body**:
  ```json
  {
    "currentPassword": "old-password",
    "newPassword": "new-password"
  }
  ```

### 3. Prerequisites

Before testing, ensure:

1. **You are logged in**:
   - First, send a request to `POST /login`
   - This will automatically save the JWT token to the `{{jwt}}` variable
   - The Authorization header will be automatically added

2. **Application is running**:
   - Start the Bus Map API application
   - Verify it's running on the configured port (default: 8080)

### 4. Update the Request Body

Replace the placeholder passwords with actual values:

```json
{
  "currentPassword": "your-current-password",
  "newPassword": "your-new-password"
}
```

### 5. Send the Request

Click **Send** in Postman

### Expected Responses

#### Success Response (200 OK):
```json
"Password changed successfully"
```

#### Invalid Current Password (401 Unauthorized):
```json
"Old password is incorrect"
```

#### Missing Fields (400 Bad Request):
```json
"Old and new passwords are required"
```

#### Not Authenticated (401/403):
```json
"Unauthorized" or "Access Denied"
```

#### User Not Found (404 Not Found):
```json
"User not found"
```

## What Happens When You Send the Request

1. **Authenticates** the user using the JWT token in the Authorization header
2. **Validates** both currentPassword and newPassword are provided
3. **Retrieves** the authenticated user from the database
4. **Verifies** the current password matches the stored password
5. **Updates** the password with the new hashed password
6. **Saves** the updated user to the database

## Testing the Flow

### Complete Test Scenario:

1. **Login to get JWT token**:
   ```json
   POST /login
   {
     "username": "testuser",
     "password": "currentpassword123"
   }
   ```
   - Response will contain a JWT token
   - Token is automatically saved to `{{jwt}}` variable

2. **Change the password**:
   ```json
   POST /change-password
   Headers: Authorization: Bearer {{jwt}}
   {
     "currentPassword": "currentpassword123",
     "newPassword": "newpassword456"
   }
   ```
   - Should return: "Password changed successfully"

3. **Verify the password was changed** by logging in with the new password:
   ```json
   POST /login
   {
     "username": "testuser",
     "password": "newpassword456"
   }
   ```
   - Should successfully return a new JWT token

4. **Try logging in with old password** (should fail):
   ```json
   POST /login
   {
     "username": "testuser",
     "password": "currentpassword123"
   }
   ```
   - Should return: "Invalid credentials"

## Troubleshooting

### "Unauthorized" Error?

**Cause**: No JWT token or invalid token

**Solution**:
1. Make sure you logged in first using `POST /login`
2. Check that the JWT token was saved to `{{jwt}}` variable
3. Verify the Authorization header is present: `Bearer {{jwt}}`
4. The token may have expired - login again to get a new one

### "Old password is incorrect"?

**Cause**: The currentPassword doesn't match the stored password

**Solution**:
1. Double-check you're using the correct current password
2. Remember passwords are case-sensitive
3. If you forgot your password, use the `POST /forgot-password` endpoint

### "User not found"?

**Cause**: The authenticated user doesn't exist in the database

**Solution**:
1. Verify your JWT token is valid
2. Check that the user hasn't been deleted
3. Try logging in again with `POST /login`

## Security Notes

- ✅ **Requires Authentication** - You must be logged in (JWT token required)
- ✅ **Verifies Current Password** - Ensures the user knows the current password
- ✅ **Passwords are Hashed** - Passwords are encrypted before storage
- ✅ **Changes Only Your Password** - You can only change your own password (based on JWT)
- ⚠️ **Use HTTPS in Production** - Passwords should only be sent over secure connections

## Password Requirements

Currently, the endpoint accepts any password. Consider implementing password requirements such as:
- Minimum length (e.g., 8 characters)
- Mix of uppercase and lowercase
- At least one number
- At least one special character
- Not the same as the current password
