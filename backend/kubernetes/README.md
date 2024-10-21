### Apply all kubernetes resources
```
kubectl apply -f ./namespace -f ./config-maps -f ./deployments -f ./services -f ./secrets -f ./hpa -n elderspy-ns
kubectl delete deployments --all -n elderspy-ns
```