<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Sign In</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5 w-50">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center mb-4">Sign In</h2>
            <form action="" method="post">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <!-- Element for error messages-->
                <p style="position: fixed; bottom: 350px; color: red; font-size: 12px;">
                </p>
                <button type="submit" class="btn btn-primary btn-block" style="background-color: black; border-color: black">Sign In</button>
            </form>
            <p class="mt-3 text-center">Don't have an account? <a href="sign-up.jsp">Sign Up</a></p>
        </div>
    </div>
</div>
</body>
</html>

