
(ns todo.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]
            [clojure.string :as str]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

;; --- 1. O CÉREBRO REATIVO ---
(defonce app-state
  (r/atom {:input-text ""
           :todos []
           :loading false
           :error nil}))

;; --- 2. URL BASE DA API ---
(def api-url "http://localhost:3000/api")

;; --- 3. FUNÇÃO AUXILIAR: FETCH JSON ---
(defn fetch-json [url options]
  (-> (js/fetch url (clj->js options))
      (.then (fn [response]
               (when-not (.-ok response)
                 (throw (js/Error.
                         (str "HTTP error: " (.-status response)))))
               (.json response)))
      (.then #(js->clj % :keywordize-keys true))))

;; --- 4. BUSCAR TODAS AS TAREFAS ---
(defn get-todos []
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (let [response
            (<p! (fetch-json (str api-url "/todos")
                             {:method "GET"}))]
        (swap! app-state assoc
               :todos (:todos response)
               :loading false))
      (catch js/Error e
        (swap! app-state assoc
               :error (.-message e)
               :loading false)))))

;; --- 5. CRIAR UMA TAREFA ---
(defn create-todo [todo-data]
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (<p! (fetch-json
            (str api-url "/todos")
            {:method "POST"
             :headers {"Content-Type" "application/json"}
             :body (js/JSON.stringify (clj->js todo-data))}))
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc
               :error (.-message e)
               :loading false)))))

;; --- 6. ALTERNAR STATUS DA TAREFA ---
(defn toggle-todo [todo-id]
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (<p! (fetch-json
            (str api-url "/todos/" todo-id "/toggle")
            {:method "POST"}))
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc
               :error (.-message e)
               :loading false)))))

;; --- 7. EXCLUIR UMA TAREFA ---
(defn delete-todo [todo-id]
  (swap! app-state assoc :loading true :error nil)
  (go
    (try
      (<p! (fetch-json
            (str api-url "/todos/" todo-id)
            {:method "DELETE"}))
      (get-todos)
      (catch js/Error e
        (swap! app-state assoc
               :error (.-message e)
               :loading false)))))

;; --- 8. FORMULÁRIO ---
(defn todo-form []
  [:div.todo-input

   [:input
    {:type "text"
     :placeholder "O que precisa ser feito?"
     :value (:input-text @app-state)
     :on-change
     #(swap! app-state assoc
             :input-text (-> % .-target .-value))}]

   [:button
    {:on-click
     (fn []
       (let [title (:input-text @app-state)]
         (when-not (str/blank? title)
           (create-todo {:title title})
           (swap! app-state assoc :input-text ""))))}
    "Adicionar"]])

;; --- 9. LISTA DE TAREFAS ---
(defn todo-list []
  [:ul.todo-list
   (for [todo (:todos @app-state)]
     ^{:key (:todos/id todo)}
     [:li.todo-item
      {:class (when (= 1 (:todos/completed todo))
                "completed")}

      ;; Checkbox
      [:input.todo-checkbox
       {:type "checkbox"
        :checked (not= 0 (:todos/completed todo))
        :on-change
        #(toggle-todo (:todos/id todo))}]

      ;; Título
      [:span.todo-title
       (:todos/title todo)]

      ;; Botão de exclusão
      [:button.delete-btn
       {:type "button"
        :title "Excluir tarefa"
        :on-click
        #(delete-todo (:todos/id todo))}
       "×"]])])

;; --- 10. APLICAÇÃO PRINCIPAL ---
(defn app []
  [:div.todo-app

   [:h1 "Todo App"]

   [:p "Conectado à API. Os dados sobrevivem ao F5!"]

   (when (:loading @app-state)
     [:p "Carregando todos..."])

   (when-let [err (:error @app-state)]
     [:p.error (str "Erro: " err)])

   [todo-form]

   [todo-list]])

;; --- 11. INICIALIZAÇÃO ---
(defn ^:export init []
  (println "Frontend inicializado...")

  (let [root
        (rdom/create-root
         (js/document.getElementById "app"))]

    (.render root (r/as-element [app])))

  (get-todos))
