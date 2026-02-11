# Despliegue en GCP (Cloud Run) - admin

Este documento explica cómo desplegar el servicio `admin` en Google Cloud Run usando la cuenta gratuita / capa gratuita.

Requisitos previos
- Tener una cuenta de Google Cloud y un proyecto configurado.
- Tener instalado y configurado `gcloud` (SDK).
- Habilitar APIs: Cloud Run, Cloud Build, Artifact Registry o Container Registry.

Pasos rápidos (Cloud Build + Cloud Run)
1. Autenticar gcloud y seleccionar proyecto:

```powershell
gcloud auth login
gcloud config set project YOUR_PROJECT_ID
```

2. (Opcional) Habilitar APIs:

```powershell
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
```

3. Construir y desplegar con Cloud Build (usa `cloudbuild.yaml`):

```powershell
cd admin
gcloud builds submit --config cloudbuild.yaml --project=YOUR_PROJECT_ID
```

4. Alternativa manual con Docker + gcloud:

```powershell
cd admin
# build local
docker build -t gcr.io/YOUR_PROJECT_ID/admin:latest .
# push
docker push gcr.io/YOUR_PROJECT_ID/admin:latest
# deploy
gcloud run deploy admin-service --image gcr.io/YOUR_PROJECT_ID/admin:latest --region=us-central1 --platform=managed --allow-unauthenticated --port=8080
```

Notas
- El Dockerfile está optimizado para Cloud Run (multi-stage). Ajusta `JAVA_OPTS` si necesitas más memoria.
- El seed de datos se ejecuta sólo con el profile `dev`. Para producción evita activar `dev`.
- El plan gratuito de Cloud Run permite instancias con CPU/Memory limit; ajusta la memoria si detectas OOM.

Problemas comunes
- Error de permisos push: `gcloud auth configure-docker`.
- Si usas Artifact Registry en lugar de Container Registry adapta el nombre del repositorio.

Contacto
- Si quieres, puedo generar también `app.yaml` para App Engine, o adaptar para Artifact Registry.
