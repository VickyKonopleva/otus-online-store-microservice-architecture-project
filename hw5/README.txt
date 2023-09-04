hw5

ISTIO

cd istio-1.18.2
export PATH=$PWD/bin:$PATH
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled

APP
in folder crudeapp-chart:

Helm package .

helm repo update

helm install release-1 /Users/viktoriakonopleva/desktop/otus-hw/k8s/hw5/crudeapp-chart/crudeapp-0.1.0.tgz -f values.yaml

minikube tunnel

VIEW the DASHBOARDS
from root:

cd istio
kubectl apply -f samples/addons
kubectl rollout status deployment/kiali -n istio-system
istioctl dashboard kiali
