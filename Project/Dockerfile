# 1. Use the official Python image
FROM python:3.9

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the dependencies file first 
COPY requirements.txt .

# 4. Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copy the rest of the app files
COPY . .

# 6. Expose port 5000 to the host
EXPOSE 5000

# 7. Run the Flask app
CMD ["python", "app.py"]
