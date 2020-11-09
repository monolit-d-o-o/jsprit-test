login [openid-connect]

curl -X POST 'https://perpro.sledenje.com:8443/auth/realms/omniopti/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=password' \
 --data-urlencode 'client_id=angular' \
 --data-urlencode '=test' \
 --data-urlencode 'password=test'


rest api

 --run problem
http://localhost:8888/api/test/hvrp/50

--list runing tasks
http://localhost:8888/api/test/hvrp/tasklist

--stop task by id
http://localhost:8888/api/test/hvrp/cancel/662236a0-a8ce-4f95-8453-84f87586ab9e

-- show result
http://localhost:8888/api/test/hvrp/result/bad4a5f4-a674-415c-8e55-01ab95458667



settings 

application.yaml