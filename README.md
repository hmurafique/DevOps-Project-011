# DevOps-Project-011 
# CI/CD Pipeline for Spring Boot Application using Jenkins, SonarQube, Docker, ArgoCD & Kubernetes

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.16-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-Code%20Quality-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-29.5.3-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Minikube-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Helm](https://img.shields.io/badge/Helm-v4.2.0-0F1689?style=for-the-badge&logo=helm&logoColor=white)
![ArgoCD](https://img.shields.io/badge/ArgoCD-GitOps-EF7B4D?style=for-the-badge&logo=argo&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)

This project implements a complete CI/CD Pipeline for a Spring Boot Application, integrating code quality analysis, containerization, and GitOps-based deployment to Kubernetes.

## 🏗️ Architecture

```
GitHub Push
    ↓
Jenkins (Build & Test)
    ↓
Maven Build → Unit Tests
    ↓
SonarQube Analysis
    ↓
Docker Build → DockerHub Push
    ↓
Update K8s Manifests (GitHub)
    ↓
ArgoCD (GitOps Sync)
    ↓
Kubernetes (Minikube) Deploy
```

## 🖥️ Infrastructure

| Server | Type | Purpose |
|---|---|---|
| Jenkins-Server | t2.large (Ubuntu 22.04) | Jenkins, Maven, Docker, SonarQube |
| Kubernetes-Server | t2.medium (Ubuntu 22.04) | Minikube, kubectl, Helm, ArgoCD |

## 🛠️ Tools & Versions

- Java 21 (Eclipse Temurin)
- Jenkins (latest)
- Maven 3.9.16
- SonarQube (Docker container)
- Docker 29.5.3
- Minikube v1.38.1
- kubectl v1.36.1
- Helm v4.2.0
- ArgoCD (via Helm chart)

## 📂 Repository Structure

```
DevOps-Project-011/
├── spring-boot-app/
│   ├── src/main/java/com/example/
│   │   ├── Application.java
│   │   └── HelloController.java
│   ├── src/test/java/com/example/
│   │   └── ApplicationTest.java
│   ├── pom.xml
│   ├── Dockerfile
│   └── Jenkinsfile
└── spring-boot-app-manifests/
    └── deployment.yml
```

## ⚙️ Setup Steps

### 1. Provision Infrastructure
Launch two EC2 instances as per the table above with required security group ports (22, 8080, 9000 for Jenkins-Server; 22, 8080, 30000-32767 for Kubernetes-Server).

### 2. Jenkins-Server Setup
- Install Java 21, Jenkins, Docker, and run SonarQube as a Docker container
- Install Jenkins plugins: Eclipse Temurin Installer, SonarQube Scanner, Docker, Docker Pipeline, Pipeline Stage View
- Configure JDK21 and Maven3 tools in Jenkins
- Generate SonarQube token and configure SonarQube server in Jenkins
- Add GitHub and DockerHub credentials in Jenkins

### 3. Kubernetes-Server Setup
- Install Docker, Minikube, kubectl, and Helm
- Start Minikube with Docker driver
- Install ArgoCD via Helm into the `argocd` namespace
- Expose ArgoCD server as NodePort and access via `kubectl port-forward`

### 4. Application Code
Spring Boot REST application with `/` (welcome page) and `/health` endpoints, packaged via Maven and containerized using a multi-stage-free Alpine JRE Dockerfile.

### 5. Jenkins Pipeline
The `Jenkinsfile` (using `agent any` with JDK21 & Maven3 tools) performs:
1. **Checkout** — pulls source from GitHub
2. **Build** — `mvn clean package -DskipTests`
3. **Test** — `mvn test`
4. **SonarQube Analysis** — `mvn sonar:sonar`
5. **Build Docker Image** — tags with `BUILD_NUMBER`
6. **Push Docker Image** — pushes `:BUILD_NUMBER` and `:latest` to DockerHub
7. **Update K8s Manifests** — updates `deployment.yml` image tag and pushes to GitHub

### 6. ArgoCD Application
ArgoCD Application configured with:
- **Repo**: `https://github.com/hmurafique/DevOps-Project-011`
- **Path**: `spring-boot-app-manifests`
- **Cluster**: `https://kubernetes.default.svc`
- **Namespace**: `default`
- **Sync Policy**: Automatic with Self Heal

ArgoCD automatically detects manifest changes pushed by Jenkins and syncs the deployment to the Minikube cluster.

## 🚀 Verification

```bash
kubectl get pods
kubectl get svc spring-boot-app-service
curl http://$(minikube ip):32000
```

Expected output: a welcome HTML page confirming the app was deployed via the CI/CD pipeline.

## 🔧 Issues Faced & Fixes

| Issue | Fix |
|---|---|
| `docker: not found` inside Jenkins Docker agent | Switched pipeline `agent` from Docker container to `agent any` with JDK21/Maven3 tools |
| Maven clean failure due to root-owned files from earlier Docker-agent run | `sudo chown -R jenkins:jenkins /var/lib/jenkins/workspace/` and `sudo chmod 666 /var/run/docker.sock` |

## ✅ Result

A fully automated GitOps Pipeline: Every push to `main` triggers a build, test, code quality scan, image build/push, manifest update, and automatic Kubernetes deployment via ArgoCD — with zero manual `kubectl apply`.
