# Docker & CI/CD Quick Start

## Checklist

- ✅ `Dockerfile` created (multi-stage, production-ready)
- ✅ `.dockerignore` created (optimized build context)
- ✅ `.github/workflows/docker-publish.yml` created (automated CI/CD)
- ✅ Maven build verified (jar created successfully)
- ✅ Documentation complete

## 5-Minute Setup

### Step 1: Verify Local Build Works (Optional)

If Docker is available on your system:

```bash
cd /home/rich/Documentos/sptech/extensao/backend_grupo4
docker build -t back-end-pi:test .
```

**Expected**: Image builds and completes in ~2-5 minutes (first run downloads dependencies).

### Step 2: Push to GitHub

```bash
git add Dockerfile .dockerignore .github/workflows/docker-publish.yml DOCKER.md
git commit -m "chore: add Docker & CI/CD pipeline"
git push origin main
```

### Step 3: GitHub Actions Runs Automatically

1. Open your GitHub repository
2. Go to **Actions** tab
3. Watch the **"Build and Push Docker image"** workflow execute
4. On success, your image appears on Docker Hub as `yourusername/back-end-pi:latest`

### Step 4: Pull and Run from Docker Hub

```bash
docker pull yourusername/back-end-pi:latest
docker run -p 8080:8080 yourusername/back-end-pi:latest
```

## Key Files Overview

| File | Purpose |
|------|---------|
| `Dockerfile` | Defines build & runtime environment |
| `.dockerignore` | Excludes unnecessary files (keeps context small) |
| `.github/workflows/docker-publish.yml` | Automates build & push on git push |
| `DOCKER.md` | Comprehensive Docker documentation |

## What Happens Behind the Scenes

```
You push to GitHub
    ↓
GitHub Actions triggers
    ↓
Maven compiles & packages jar (skips tests)
    ↓
Docker multi-stage builds image
    ↓
Image pushed to Docker Hub
    ↓
Tagged as: latest, sha-<commit>, <branch>
```

## Production Deployment

Once your image is on Docker Hub, deploy it anywhere:

```bash
# Docker Compose
version: '3.8'
services:
  backend:
    image: yourusername/back-end-pi:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/app
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: pass
      JAVA_OPTS: "-Xmx1g -Xms512m"

# Kubernetes
kubectl create deployment backend --image=yourusername/back-end-pi:latest
kubectl expose deployment backend --type=LoadBalancer --port=8080
```

## Troubleshooting

**Pipeline fails on GitHub?**
- Check GitHub Actions logs for error details
- Verify `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets exist
- Ensure branch is `main` or `master` (hardcoded in workflow)

**Image doesn't run locally?**
- Verify database connection details in environment variables
- Check Spring profile matches your environment (dev/prod)
- Review application logs: `docker logs <container>`

## Next: Customize & Optimize

1. **Update label metadata** in `Dockerfile` (lines 19-21)
2. **Adjust JAVA_OPTS** for production memory sizing
3. **Add health checks** for Docker/Kubernetes orchestration
4. **Set up branch protection** to enforce successful CI/CD runs before merging

See `DOCKER.md` for comprehensive details.

---

**Status**: ✅ Production-ready Docker pipeline configured

