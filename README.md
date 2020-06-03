# Atraaf

Atraaf is an open-source parameter manager based on environments.

## Users
### sign up example:

```bash
curl --location --request POST '/user' \
--header 'Content-Type: application/json' \
--data-raw '{
	"email":"me@mail.com",
	"password":"pass",
	"username":"myusername",
	"ssoid":"123",
	"podToken":"abc"
}'
 ```

###  get current user info:

```bash
curl --location --request GET '/user'
```

###  get new token example:

```bash
curl --location --request POST '/user/token' \
--header 'Content-Type: application/json' \
--data-raw '{
	"username":"myusername",
	"password":"pass",
	"podToken":"abc"
}'
```
## Applications
### create application example:

```bash
curl --location --request POST '/application' \
--header 'Authorization: Bearer  XXX' \ #got from getToken service
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "core",
    "description": "sakku core"
}'
```

### get application by id example:

```bash
curl --location --request GET '/application/1' \
--header 'Authorization: Bearer  XXX' 
```

### get all applications:

```bash
curl --location --request GET '/application' \
--header 'Authorization: Bearer  XXX' 
```
## Environmets
### create environments for an application example:

```bash
curl --location --request POST '/application/1' \
--header 'Authorization: Bearer  XXX' \
--header 'Content-Type: application/json' \
--data-raw '{
	"name":"development"
}'
 ```

### get application environment by id example:

```bash
curl --location --request GET '/application/1/1' \ # {appID}/{envID}
--header 'Authorization: Bearer  XXX' 
```



### create parameter for application example:

```bash
curl --location --request POST '/application/1/1' \
--header 'Authorization: Bearer XXX' \
--header 'Content-Type: application/json' \
--data-raw '[
   {
      "key":"first.key.name",
      "value":"first.keyVlue",
      "global":true # this param is available for all envs
   },
   {
      "key":"second.key.name",
      "value":"second.keyVlue",
      "global":false 
   }

]'
```


## Download Configs
### get config file example:

```bash
curl --location --request GET '/config/1/1' \ #{appID}/{envID}
--header 'key: XXXX-XXXXX'  # got from getEnvByID.result.accessKey
 ```

### get config file in map format example:

```bash
curl --location --request GET '/config/1/1/map' \
--header 'key: ebcbe883-fda3-49d1-bb15-ea9035dcf829' 
```

