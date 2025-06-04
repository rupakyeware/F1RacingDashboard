# F1 Dashboard - Windows Solution Guide

If you're experiencing issues running the F1 Dashboard on Windows, follow this troubleshooting guide to get it working.

## Common Issues

1. **Java Compilation Errors**: Files don't compile together even though they compile individually
2. **MySQL Connection Errors**: Cannot connect to the MySQL database
3. **Class Not Found Errors**: Java can't find certain classes at runtime

## Solution Steps

### Step 1: Verify Your Environment

1. Ensure Java is properly installed:
   ```
   java -version
   javac -version
   ```

2. Verify MySQL is running:
   - Open MySQL Workbench
   - Connect to your local server
   - Verify the `f1dashboard` database exists (run `create_f1_database.sql` if needed)

### Step 2: Choose the Right Approach

Based on your error messages, try these approaches in order:

#### A. Simplified Dashboard

If you're having issues with the full dashboard, try the simplified version:

1. Run `run_simple_dashboard.bat`
2. This will launch a simpler version with just the track map component
3. If this works, you can gradually add more components

#### B. Component-by-Component Testing

If the simple dashboard fails:

1. Try `run_historical_test.bat` to test just the historical comparison panel
2. Try `test_track.bat` to test just the track map panel
3. These isolated tests can help identify which component is causing problems

#### C. Comprehensive Compilation

For thorough debugging:

1. Run `run_f1_dashboard.bat`
2. This script compiles each component individually with verbose output
3. It will identify exactly which file is causing compilation errors

### Step 3: Database Configuration

If you're getting database errors:

1. Edit `F1DatabaseManager.java`:
   - Make sure the password matches your MySQL password: `"1234"`
   - The database URL should be: `"jdbc:mysql://localhost:3306/f1dashboard"`
   - User should be: `"root"` (or your MySQL username)

2. Verify database creation:
   - Open MySQL Workbench
   - Run the `create_f1_database.sql` script
   - Verify tables are created with: `SHOW TABLES FROM f1dashboard;`

### Step 4: Class Path and Library Issues

If you get "Class not found" errors:

1. Ensure the libraries are properly downloaded:
   - Check that `lib/mysql-connector-j.jar` exists
   - Check that `lib/json-20231013.jar` exists

2. Make sure you're using the Windows-specific classpath format:
   - Windows uses semicolons (`;`) not colons (`:`) in classpaths

## Complete Solution

If all else fails, try this complete rebuild:

1. Create a new empty directory
2. Copy all `.java` files to the new directory
3. Create a `lib` directory inside it
4. Run `run_f1_dashboard.bat` to build from scratch

## Need More Help?

If you continue to experience issues:

1. Copy the exact error messages
2. Note which step you're having trouble with
3. Check the Java and MySQL log files