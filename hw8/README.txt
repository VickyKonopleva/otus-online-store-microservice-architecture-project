hw6

KAFKA
install KAFKA
kreate topic user_state

minikube start --memory 7400 --cpus 4

kubectl create namespace kafka
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl get pod -n kafka --watch
//wait
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka 
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka 

ISTIO

cd istio-1.18.2
export PATH=$PWD/bin:$PATH
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled

REDIS
helm install redis-service --set auth.password=VgUQ9mjbH3 bitnami/redis
get redis password
export REDIS_PASSWORD=$(kubectl get secret --namespace default redis-test -o jsonpath="{.data.redis-password}" | base64 --decode)
set password to oreders-chart configmap

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
newman run Otus-hw8.postman_collection.json

