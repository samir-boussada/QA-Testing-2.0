# 🌐 Hostinger VPS Deployment Guide — NovaShop E-Commerce

This guide explains step-by-step how to deploy and host the **NovaShop E-Commerce Platform** on a **Hostinger Linux VPS (Ubuntu 22.04 / 24.04 LTS)** with MySQL, a systemd auto-restart service, and an Nginx reverse proxy with free SSL (Let's Encrypt).

---

## 📋 Architecture on Hostinger VPS

```
Internet (HTTPS :443) ➔ Nginx Reverse Proxy (SSL) ➔ Spring Boot App (:8080) ➔ MySQL DB (:3306)
```

---

## Step 1: Connect to Your Hostinger VPS

1. Open your terminal or PowerShell.
2. Connect via SSH using the IP address provided in your Hostinger VPS control panel:
   ```bash
   ssh root@YOUR_HOSTINGER_VPS_IP
   ```

---

## Step 2: Install Java 17, MySQL, and Nginx

Update packages and install the runtime stack:
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-17-jdk openjdk-17-jre mysql-server nginx certbot python3-certbot-nginx git
```

Verify Java version:
```bash
java -version
# Should display: openjdk version "17.0.x"
```

---

## Step 3: Configure MySQL Database

1. Secure your MySQL installation:
   ```bash
   sudo mysql_secure_installation
   ```
2. Log into MySQL:
   ```bash
   sudo mysql -u root -p
   ```
3. Create the database and dedicated user:
   ```sql
   CREATE DATABASE novashop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'novashop_user'@'localhost' IDENTIFIED BY 'YOUR_SECURE_PASSWORD';
   GRANT ALL PRIVILEGES ON novashop_db.* TO 'novashop_user'@'localhost';
   FLUSH PRIVILEGES;
   EXIT;
   ```

---

## Step 4: Build and Upload the Application JAR

1. On your local development machine, package the executable JAR:
   ```bash
   cd C:\Users\Samir\.gemini\antigravity\scratch\novashop-ecommerce
   mvn clean package -DskipTests
   ```
   *(The JAR is generated at `target/novashop-ecommerce-1.0.0.jar`)*.

2. Upload the JAR to your Hostinger VPS:
   ```bash
   scp target/novashop-ecommerce-1.0.0.jar root@YOUR_HOSTINGER_VPS_IP:/opt/novashop/novashop.jar
   ```

---

## Step 5: Configure Systemd Service (Auto-Restart on Reboot)

Create a system service so NovaShop runs continuously in the background and restarts automatically if the server reboots:

1. Copy the provided `deployment/systemd-novashop.service` to:
   ```bash
   sudo nano /etc/systemd/system/novashop.service
   ```
2. Paste the configuration:
   ```ini
   [Unit]
   Description=NovaShop E-Commerce Spring Boot Application
   After=syslog.target network.target mysql.service

   [Service]
   User=root
   WorkingDirectory=/opt/novashop
   ExecStart=/usr/bin/java -jar /opt/novashop/novashop.jar --spring.profiles.active=prod
   SuccessExitStatus=143
   Restart=always
   RestartSec=10

   [Install]
   WantedBy=multi-user.target
   ```
3. Enable and start the service:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable novashop
   sudo systemctl start novashop
   sudo systemctl status novashop
   ```

---

## Step 6: Configure Nginx Reverse Proxy & Free SSL

1. Copy the provided `deployment/nginx-reverse-proxy.conf` into:
   ```bash
   sudo nano /etc/nginx/sites-available/novashop
   ```
2. Paste:
   ```nginx
   server {
       listen 80;
       server_name yourdomain.com www.yourdomain.com;

       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
3. Enable site and test configuration:
   ```bash
   sudo ln -s /etc/nginx/sites-available/novashop /etc/nginx/sites-enabled/
   sudo nginx -t
   sudo systemctl restart nginx
   ```

4. Install Free SSL with Let's Encrypt Certbot:
   ```bash
   sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
   ```

🎉 **Your NovaShop E-Commerce store is now live on Hostinger with full HTTPS protection!**
