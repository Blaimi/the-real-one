#!/bin/sh

podman build -t example.com/alocalimage:local-dev .
podman save example.com/alocalimage:local-dev --format oci-archive -o alocalimage.tar
kind load image-archive alocalimage.tar -n kind-cluster
kubectl apply -k ./dev --prune -l app.kubernetes.io/part-of=biletado -n biletado
kubectl wait pods -n biletado -l app.kubernetes.io/part-of=biletado --for condition=Ready --timeout=120s
