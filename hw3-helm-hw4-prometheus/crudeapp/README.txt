hw3

To deploy the app run the following inside crudeapp-chart folder:

helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx --create-namespace
minikube addons enable ingress

Helm package .

helm repo update

helm install release-1 crudeapp-0.1.0.tgz -f values.yaml

Port-forwarding
kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8000:80 

check: 
curl --resolve "arch.homework:8000:127.0.0.1" -i http://arch.homework:8000/otusapp/victoria/users/1

hw4 

To deploy the app run the following inside crudeapp folder:

INGRESS

helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --set controller.metrics.enabled=true \
  --set controller.metrics.serviceMonitor.enabled=true 
minikube addons enable ingress

PROMETHEUS

kubectl create ns monitoring
kubectl config set-context --current --namespace=monitoring
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://charts.helm.sh/stable
helm repo update
helm install prometheus prometheus-community/kube-prometheus-stack -f prometheus.yaml --atomic

kubectl port-forward service/prometheus-grafana 9000:80 

kubectl port-forward service/prometheus-kube-prometheus-prometheus 9090

APP
in folder crudeapp-chart:

Helm package .

helm repo update

helm install release-1 /Users/viktoriakonopleva/desktop/otus-hw/k8s/hw3-helm-hw4-prometheus/crudeapp/crudeapp-chart/crudeapp-0.1.0.tgz -f values.yaml

Port-forwarding
kubectl port-forward --namespace=monitoring service/ingress-nginx-controller 8000:80 
OR
minikube tunnel 