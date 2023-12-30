<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Error</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
      .error-container {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
        text-align: center;
      }
      .error-message {
        font-size: 18px;
        margin-bottom: 20px;
      }
    </style>
</head>
<body>
<div class="container error-container">
    <div>
        <h2 class="error-message">Error on the server occurred</h2>
        <a href="redirect"><button class="btn btn-primary">Go back</button></a>
    </div>
</div>
</body>
</html>
