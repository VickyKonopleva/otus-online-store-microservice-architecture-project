hw6

KAFKA
install KAFKA
kreate topic user_state

ISTIO

cd istio-1.18.2
export PATH=$PWD/bin:$PATH
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled

APP
in folder users-chart:

Helm package .

helm repo update

helm install users-release users-0.1.0.tgz -f values.yaml

in folder orders-chart:

Helm package .

helm repo update

helm install orders-release orders-0.1.0.tgz -f values.yaml

GATEWAY
kubectl apply -f gateway.yaml

minikube tunnel

VIEW the DASHBOARDS
from root:

cd istio
kubectl apply -f samples/addons
kubectl rollout status deployment/kiali -n istio-system
istioctl dashboard kiali

RUN REQUEST COLLECTION

npm install -g newman
newman run Otus-hw6.postman_collection.json

