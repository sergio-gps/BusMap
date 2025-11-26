# Secrets Configuration

This project uses a `secrets.properties` file to store sensitive credentials like Gmail SMTP passwords.

## Quick Setup

1. **Copy the template file**:
   ```bash
   cd src/main/resources
   copy secrets.properties.example secrets.properties
   ```

2. **Edit `secrets.properties`** with your real credentials:
   ```properties
   gmail.username=your-actual-email@gmail.com
   gmail.password=your-actual-app-password
   ```

3. **Done!** The file is already in `.gitignore` and won't be committed.

## Security Notes

- ✅ `secrets.properties` is in `.gitignore` - **SAFE to use**
- ✅ Only the example template is committed to Git
- ⚠️ **NEVER** commit `secrets.properties` with real credentials
- 🔒 Each developer needs their own `secrets.properties` file locally

## Getting Gmail App Password

See [EMAIL_SETUP.md](EMAIL_SETUP.md) for detailed instructions on how to:
1. Enable 2-Factor Authentication on Gmail
2. Generate an App Password
3. Configure the forgot password feature

## Troubleshooting

**File not found error?**
- Make sure `secrets.properties` exists in `src/main/resources/`
- The application uses `spring.config.import=optional:classpath:secrets.properties`
- The `optional:` prefix means the app will start even if the file is missing (but email won't work)

**Email not sending?**
- Verify your Gmail App Password is correct
- Check that 2FA is enabled on your Google account
- Review the application logs for error details
