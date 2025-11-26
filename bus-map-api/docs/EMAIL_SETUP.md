# Email Configuration for Forgot Password Feature

## Gmail SMTP Configuration

The forgot password feature uses Gmail SMTP to send password reset emails. You need to configure your Gmail credentials to enable this functionality.

### Steps to Set Up Gmail App Password

1. **Enable 2-Factor Authentication** on your Google account:
   - Go to https://myaccount.google.com/security
   - Enable 2-Step Verification

2. **Create an App Password**:
   - Go to https://myaccount.google.com/apppasswords
   - Select "Mail" and your device
   - Generate the app password
   - Copy the 16-character password (without spaces)

### Configuration Setup

The Gmail credentials are stored in a **secrets file** for security.

#### Setup Steps:

1. **Create the secrets file** from the template:
   ```bash
   # Navigate to the resources directory
   cd src/main/resources
   
   # Copy the example file (Windows)
   copy secrets.properties.example secrets.properties
   ```

2. **Edit `secrets.properties`** with your Gmail credentials:
   ```properties
   gmail.username=your-email@gmail.com
   gmail.password=your-16-char-app-password
   ```

3. **Verify `.gitignore`** includes the secrets file:
   ```
   src/main/resources/secrets.properties
   ```
   
   ✅ The secrets file is already in `.gitignore` and will NOT be committed to Git.

#### How It Works:

- `application.properties` uses Spring Boot's standard mail properties:
  ```properties
  spring.mail.host=smtp.gmail.com
  spring.mail.port=587
  spring.mail.username=${gmail.username}
  spring.mail.password=${gmail.password}
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  ```
- The credentials come from `secrets.properties`:
  ```properties
  gmail.username=your-email@gmail.com
  gmail.password=your-16-char-app-password
  ```
- Spring Boot auto-configures the `JavaMailSender` bean with these properties
- `secrets.properties.example` is a template you can commit to Git (without real credentials)

**⚠️ SECURITY**: 
- ✅ `secrets.properties` is in `.gitignore` - safe to use
- ✅ Only `secrets.properties.example` is committed to Git
- ⚠️ Never commit your actual `secrets.properties` file!

### Testing the Forgot Password Feature

1. **Ensure users have email addresses**: Update the `usuarios` table to include email addresses for testing:

```sql
UPDATE usuarios SET email = 'test@example.com' WHERE username = 'testuser';
```

2. **Test the endpoint**:

```bash
POST /forgot-password
Content-Type: application/json

{
  "email": "test@example.com"
}
```

3. **Expected behavior**:
   - A new random password (12 characters) is generated
   - The password is hashed and updated in the database
   - An email is sent to the user with the new password
   - The user can log in with the new password

### Email Template

The email sent to users includes:
- Subject: "Password Reset - Bus Map API"
- The username
- The new temporary password
- Instructions to change the password immediately

### Troubleshooting

1. **Email not sending**:
   - Verify 2FA is enabled on your Google account
   - Verify the app password is correct (16 characters, no spaces)
   - Check if Gmail SMTP is accessible from your network
   - Review application logs for detailed error messages

2. **"Less secure app access" error**:
   - This is deprecated by Google
   - You MUST use an App Password (requires 2FA)

3. **User not found**:
   - Verify the user has an email address in the database
   - The endpoint returns a generic message for security (doesn't reveal if email exists)

### Production Considerations

1. **Use environment variables** for credentials
2. Consider using **Spring Cloud Config** or **HashiCorp Vault** for secrets management
3. Implement **rate limiting** to prevent abuse of the forgot password endpoint
4. Add **email verification** before allowing password reset
5. Consider implementing **password reset tokens** instead of sending passwords via email
6. Add **logging and monitoring** for security audit

### Database Migration

Don't forget to add the email column to the usuarios table:

```sql
ALTER TABLE usuarios ADD COLUMN email VARCHAR(255) UNIQUE;
```

The application uses `spring.jpa.hibernate.ddl-auto=update`, so it should automatically add the column when you restart the application.
