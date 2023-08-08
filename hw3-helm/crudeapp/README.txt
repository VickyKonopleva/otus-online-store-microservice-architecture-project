To deploy the app run the following:

helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx --create-namespace
minikube addons enable ingress

Helm package .

helm repo update

helm install release-1 crudeapp-0.1.0.tgz -f values.yaml

Port-frowarding
kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8000:80 

check: 
curl --resolve "arch.homework:8000:127.0.0.1" -i http://arch.homework:8000/otusapp/victoria/users/1