# Todo App | Clojure e ClojureScript

Aplicação de gerenciamento de tarefas desenvolvida utilizando Clojure no backend e ClojureScript no frontend. O projeto implementa uma API REST, integração entre frontend e backend e persistência de dados utilizando SQLite.

## Sobre o projeto

O Todo App permite que o usuário gerencie suas tarefas por meio de uma interface web.

A aplicação foi desenvolvida com separação entre backend e frontend. O backend disponibiliza uma API REST responsável pelas operações de dados, enquanto o frontend consome essa API utilizando requisições HTTP.

O banco de dados SQLite garante que as tarefas permaneçam salvas mesmo após atualizar a página ou reiniciar a aplicação.

## Tecnologias utilizadas

### Backend
- Clojure 1.11.1
- Ring
- Jetty
- Reitit
- SQLite
- next.jdbc
- ring-json
- ring-cors

### Frontend
- ClojureScript
- Reagent
- Shadow-CLJS
- JavaScript Fetch API
- core.async

### Ferramentas e ambiente
- Windows
- PowerShell 7
- Java Temurin JDK 17
- Clojure CLI / deps.clj
- Node.js e npm
- Git e GitHub

## Funcionalidades

- [x] Criar novas tarefas.
- [x] Listar tarefas cadastradas.
- [x] Marcar tarefas como concluídas.
- [x] Desmarcar tarefas concluídas.
- [x] Excluir tarefas.
- [x] Integração entre frontend e API REST.
- [x] Persistência dos dados utilizando SQLite.
- [x] Recuperação das tarefas após atualizar a página.

## Estrutura do projeto

```text
todo-app/
│
├── src/
│   └── todo/
│       ├── backend/
│       │   ├── core.clj
│       │   ├── db.clj
│       │   └── handler.clj
│       │
│       └── frontend/
│           └── core.cljs
│
├── resources/
├── deps.edn
├── .gitignore
└── README.md
```

O arquivo `prod.db` é criado automaticamente na raiz do projeto durante a inicialização do backend, caso ainda não exista.

## Requisitos para execução

O ambiente utilizado no desenvolvimento foi Windows com PowerShell.

| Ferramenta | Versão utilizada / requisito |
|---|---|
| Sistema operacional | Windows |
| Terminal | PowerShell 7.7.0 |
| Java | Eclipse Temurin JDK 17.0.21 |
| Clojure CLI | deps.clj 1.12.7.1681 |
| Node.js | 18 ou superior |
| npm | Instalado junto ao Node.js |
| Git | Instalado e configurado |

### 1. Verificar as instalações

Abra o PowerShell e execute:

```powershell
java -version
javac -version
clj --version
node --version
npm --version
git --version
```

O Java utilizado durante o desenvolvimento foi o Temurin JDK 17.

O caminho configurado para o Java foi:

```text
C:\Program Files\Eclipse Adoptium\jdk-17.0.21.10002-hotspot
```

A variável `JAVA_HOME` deve apontar para o diretório do JDK instalado.

Para verificar o caminho do Java reconhecido pelo Windows:

```powershell
where.exe java
```

Caso existam várias instalações de Java, verifique se o `java -version` está utilizando a versão 17.

### 2. Clonar o repositório

No PowerShell, navegue até seu diretório de usuário:

```powershell
cd $HOME
```

Clone o repositório:

```powershell
git clone URL_DO_REPOSITORIO
```

Entre na pasta do projeto:

```powershell
cd todo-app
```

**Importante:** execute os comandos a partir da pasta do projeto. Não utilize `C:\Windows\System32` como diretório de trabalho, pois o Windows pode impedir a criação e alteração de arquivos nesse local.

### 3. Instalar as dependências do frontend

Na primeira execução, dentro da pasta do projeto, execute:

```powershell
npm install
```

Esse comando instala as dependências declaradas no projeto, quando houver um `package.json`.

As dependências Clojure declaradas em `deps.edn` são resolvidas pelo Clojure CLI durante a execução.

## Como executar a aplicação

Para executar o projeto, são necessários dois terminais do PowerShell, pois o backend e o frontend funcionam em processos separados.

### Terminal 1: Backend

Abra o primeiro terminal, entre na pasta do projeto:

```powershell
cd $HOME\todo-app
```

Inicie o servidor:

```powershell
clj -M:run
```

O backend realiza a inicialização do banco de dados SQLite e inicia o servidor Jetty na porta `3000`.

A API ficará disponível em:

```text
http://localhost:3000
```

O banco `prod.db` será criado automaticamente na raiz do projeto, caso ainda não exista.

**Não feche esse terminal enquanto estiver utilizando a aplicação.**

### Terminal 2: Frontend

Abra um segundo terminal do PowerShell e entre novamente na pasta do projeto:

```powershell
cd $HOME\todo-app
```

Inicie o ambiente de desenvolvimento do frontend:

```powershell
npx shadow-cljs watch app
```

Aguarde a compilação do Shadow-CLJS e a mensagem indicando que o build foi concluído.

O frontend ficará disponível em:

```text
http://localhost:8000
```

**Não feche esse terminal enquanto estiver utilizando a aplicação.**

### 4. Acessar a aplicação

Depois que os dois servidores estiverem funcionando, abra o navegador:

http://localhost:8000

A interface permitirá adicionar, concluir, reabrir e excluir tarefas.

## API REST

O backend disponibiliza os seguintes endpoints:

| Método HTTP | Endpoint | Funcionalidade |
|---|---|---|
| GET | `/api/hello` | Verifica a resposta inicial da API |
| GET | `/api/todos` | Lista todas as tarefas |
| POST | `/api/todos` | Cadastra uma nova tarefa |
| POST | `/api/todos/:id/toggle` | Alterna o status de conclusão |
| DELETE | `/api/todos/:id` | Exclui uma tarefa |

### Testando a API pelo PowerShell

No Windows PowerShell, o comando `curl` pode funcionar como um alias de `Invoke-WebRequest`. Por isso, para realizar as requisições HTTP, podemos utilizar diretamente `Invoke-RestMethod`.

**Listar tarefas:**

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:3000/api/todos"
```

**Criar uma tarefa:**

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:3000/api/todos" `
  -ContentType "application/json" `
  -Body '{"title":"Estudar Clojure","description":"Revisar o projeto"}'
```

**Marcar ou desmarcar uma tarefa:**

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:3000/api/todos/1/toggle"
```

**Excluir uma tarefa:**

```powershell
Invoke-RestMethod -Method Delete `
  -Uri "http://localhost:3000/api/todos/1"
```

Substitua o identificador `1` pelo ID da tarefa que deseja alterar ou excluir.

## Banco de dados SQLite

O projeto utiliza SQLite para armazenar as tarefas.

A tabela `todos` possui os seguintes campos:

| Campo | Descrição |
|---|---|
| `id` | Identificador único da tarefa |
| `title` | Título da tarefa |
| `description` | Descrição da tarefa |
| `completed` | Estado de conclusão |
| `created_at` | Data e hora de criação |

A inicialização do banco é realizada automaticamente pelo backend por meio da função `initialize-database!`, definida em `db.clj`.

A persistência permite que os registros permaneçam disponíveis mesmo após o encerramento e a inicialização dos servidores.

## Comunicação entre frontend e backend

O frontend realiza requisições HTTP para a API utilizando a função `fetch` do JavaScript.

A URL base da API está definida no frontend como:

```clojure
(def api-url "http://localhost:3000/api")
```

O backend utiliza middleware de CORS para permitir a comunicação com o frontend executado em:

```text
http://localhost:8000
```

Dessa forma, os dois serviços conseguem se comunicar durante a execução local.


## Autor

**Amanda Caroline Oliveira Jorge**

Projeto desenvolvido com finalidade acadêmica e prática de desenvolvimento utilizando o ecossistema Clojure.