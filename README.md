# Account Payable Application

Este projeto é uma aplicação para gerenciamento de contas a pagar. Ele foi desenvolvido em Java 17 e utiliza o banco de dados PostgreSQL e RabbitMQ para gerenciar as informações de contas pagas.

## Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose

## Configuração do Projeto

1. **Clonar o Repositório:**

   Clone o repositório para o seu ambiente local.

2. **Construir o Projeto:**

   Execute o comando abaixo para compilar o projeto utilizando o Maven:

   ```bash
   mvn clean install
   ```

3. **Construir e Iniciar os Contêineres Docker:**

   Utilize o Docker Compose para construir e iniciar os contêineres necessários (PostgreSQL, RabbitMQ e a aplicação):

   ```bash
   docker-compose build
   docker-compose up -d
   ```

4. **Acessar a Aplicação:**

   Após o Docker Compose ter iniciado os contêineres, a aplicação estará rodando na porta 8080.

## Endpoints da API

A aplicação oferece os seguintes endpoints para interação:

### 1. Obter valor total pago por período

Retorna o total pago em um intervalo de datas.

```bash
curl --location 'http://localhost:8080/v1/account/totalPaid?startDate=2024-01-01T00%3A00%3A00&endDate=2024-12-31T23%3A59%3A59' --header 'Content-Type: application/json'
```

### 2. Obter conta filtrando o id

Recupera as informações de uma conta específica pelo ID.

```bash
curl --location 'http://localhost:8080/v1/account/1'
```

### 3. Obter a lista de contas a pagar, com filtro de data de vencimento e descrição

Filtra as contas com base na data de vencimento e descrição. A resposta será paginada.

```bash
http://localhost:8080/v1/account/filter?dueDateStart=2024-01-01T00:00:00&dueDateEnd=2024-12-31T23:59:59&description=Pagamento&page=0&size=10
```

### 4. Cadastrar conta

Cria uma nova conta no sistema. O corpo da requisição deve ser um JSON com os dados da conta.

```bash
curl --location 'http://localhost:8080/v1/account' --header 'Content-Type: application/json' --data '{
    "dueDate": "2024-12-30T10:00:00",
    "paymentDate": "2024-12-28T12:00:00",
    "amount": 1500.00,
    "description": "Pagamento de serviços",
    "status": "ACTIVE"
}'
```

### 5. Atualizar conta

Atualiza as informações de uma conta existente pelo seu ID.

```bash
curl --location --request PUT 'http://localhost:8080/v1/account/3' --header 'Content-Type: application/json' --data '{
    "dueDate": "2024-12-30T10:00:00",
    "paymentDate": "2024-12-28T10:00:00",
    "amount": 500.00,
    "description": "Updated account description",
    "status": "ACTIVE"
}'
```

### 6.  Alterar a situação da conta

```bash
curl --location --request PATCH 'http://localhost:8080/v1/account/3/status?status=INACTIVE' \
--header 'Content-Type: application/json'
```

### 6.  Implementar mecanismo para importação de contas a pagar via arquivo cs
Arquivo para upload esta no diretorio: C:\dev\account.payable\account.payable\src\main\resources\static

```bash
curl --location 'http://localhost:8081/v1/account/import' \
--form 'fileType="csv"' \
--form 'file=@""'
```

## Tecnologias Usadas

- **Backend:** Java 21
- **Banco de Dados:** PostgreSQL
- **Mensageria:** RabbitMQ
- **Docker:** Para orquestração de contêineres
- **Maven:** Para gerenciamento de dependências e construção do projeto
