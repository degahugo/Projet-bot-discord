

# Requetes 


requete POST :  

curl -i -X POST "http://localhost:8080/api/v1/roles" \
-H "Content-Type: application/json" \
-d '{"name":"Admin"}'

requete GET :   

curl -i "http://localhost:8080/api/v1/roles"
