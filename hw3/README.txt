kubectl apply -f hw3
# у меня m1, поэтому нужно пробросить порт:
kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8000:80 
curl --resolve "arch.homework:8000:127.0.0.1" -i http://arch.homework:8000/otusapp/victoria/users/1
# если intel, то, наверное, должно сработать
 curl --resolve "arch.homework:80:$( minikube ip )" -i http://arch.homework/otusapp/victoria/users/1