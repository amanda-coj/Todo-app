(ns todo.backend.db
  "Este namespace gerencia os dados dos 'todos'
   usando um banco de dados persistente SQLite."
  (:require [next.jdbc :as jdbc]))

;; --- 1. A Configuracao (Especificacao do Banco) ---
;; O banco sera salvo em um arquivo chamado "prod.db",
;; criado automaticamente na raiz do projeto.
(def db-spec {:dbtype "sqlite"
              :dbname "prod.db"})

;; --- 2. Funcao de Inicializacao ---
;; Cria a tabela "todos" se ela ainda nao existir.
;; Vamos chama-la no 'core.clj' quando o servidor iniciar.
(defn initialize-database!
  "Cria a tabela 'todos' no banco de dados se ela nao existir."
  []
  (jdbc/execute! db-spec ["
    CREATE TABLE IF NOT EXISTS todos (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT,
      description TEXT,
      completed BOOLEAN DEFAULT 0,
      created_at TEXT
    )
  "]))

;; --- 3. As Novas Funcoes (substituindo os 'atoms') ---

(defn get-all-todos
  "Retorna uma lista com todos os 'todos' no banco."
  []
  ;; O 'execute!' roda o SQL e ja retorna mapas Clojure!
  (jdbc/execute! db-spec ["SELECT * FROM todos ORDER BY created_at DESC"]))

(defn create-todo
  "Cria um novo 'todo', salva no banco e o retorna."
  [todo-data] ;; ex: {:title "Minha tarefa", :description "..."}
  ;; A clausula 'RETURNING *' pede ao SQLite para devolver
  ;; a linha recem-inserida (com o id gerado, o completed, etc.).
  ;; 'execute-one!' retorna esse unico resultado como um mapa.
  (jdbc/execute-one! db-spec ["
    INSERT INTO todos (title, description, completed, created_at)
    VALUES (?, ?, ?, ?)
    RETURNING *"
    (:title todo-data)
    (:description todo-data)
    0 ;; completed = falso (lembre-se: SQLite usa 0/1)
    (str (java.time.Instant/now))]))