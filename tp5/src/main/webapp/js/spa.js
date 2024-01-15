/**
 * Placez ici les scripts qui seront exécutés côté client pour rendre l'application côté client fonctionnelle.
 */

// Partie 2.2 Mock Object

    // User

/*const users = [
    "users/toto",
    "users/titi"
]*/



const user1Name = {
    "name": "ALBERT"
}

const todo1 = {
    "title": "Mon beau todo",
    "hash":1276876523,
    "assignee": "user1",
    "completed":true,
    "checkBox":"&#x2611;",
    "isMe": false
}

const todo2 = {
    "title": "Mon très beau todo",
    "hash":1276876524,
    "assignee": "user1",
    "completed":false,
    "checkBox":"&#x2610;",
    "isMe": false
}

const todo3 = {
    "title": "Faire faire",
    "hash":1276876525,
    "assignee": "user2",
    "completed":false,
    "checkBox":"&#x2610;",
    "isMe": true
}

const user1AssignedTodo = {
    "assignedTodos": [
        todo1,
        todo2
    ]
}

const user1 = {
    "login": "toto",
    "name": "Test",
    user1AssignedTodo
}

let userName;
let userTodos = [];

const user2 = {
    login: "toto",
    name: "Toto",
    assignedTodos: null
}


let users = []

let connectedUser = {
    login: "",
    name: "",
    assignedTodos: []
}

let stampUser = {
    login: "",
    name: "",
    assignedTodos: []
}


let isLoged = {
    loged: false
}

    // Todo

// const todos = [todo1, todo2, todo3];



const todo1Title = {
    "title": "Test todo"
}

const todo1Assignee = {
    "assignee": "users/toto"
}

const todo1Status = {
    "status": "Done"
}

// <editor-fold desc="Gestion de l'affichage">
/**
 * Fait basculer la visibilité des éléments affichés quand le hash change.<br>
 * Passe l'élément actif en inactif et l'élément correspondant au hash en actif.
 * @param hash une chaîne de caractères (trouvée a priori dans le hash) contenant un sélecteur CSS indiquant un élément à rendre visible.
 */
function show(hash) {
    const oldActiveElement = document.querySelector(".active");
    oldActiveElement.classList.remove("active");
    oldActiveElement.classList.add("inactive");
    const newActiveElement = document.querySelector(hash.split("/")[0]);
    newActiveElement.classList.remove("inactive");
    newActiveElement.classList.add("active");
}

/**
 * Affiche pendant 10 secondes un message sur l'interface indiquant le résultat de la dernière opération.
 * @param text Le texte du message à afficher
 * @param cssClass La classe CSS dans laquelle afficher le message (défaut = alert-info)
 */
function displayRequestResult(text, cssClass = "alert-info") {
    const requestResultElement = document.getElementById("requestResult");
    requestResultElement.innerText = text;
    requestResultElement.classList.add(cssClass);
    setTimeout(
        () => {
            requestResultElement.classList.remove(cssClass);
            requestResultElement.innerText = "";
        }, 10000);
}

/**
 * Affiche ou cache les éléments de l'interface qui nécessitent une connexion.
 * @param isConnected un Booléen qui dit si l'utilisateur est connecté ou pas
 */
function displayConnected(isConnected) {
    updateLogedStatus(isConnected);

    const elementsRequiringConnection = document.getElementsByClassName("requiresConnection");
    const visibilityValue = isConnected ? "visible" : "collapse";

    Array.from(elementsRequiringConnection).forEach(element => {
        element.style.visibility = visibilityValue;
    });
}

window.addEventListener('hashchange', () => { show(window.location.hash); });

// </editor-fold>

// <editor-fold desc="Gestion des requêtes asynchrones">


/** Fonction pour vérifier si l'utilisateur est connecté
 *
 * @returns {boolean}
 */
function isConnected() {
    const jwtToken = localStorage.getItem('jwt');
    return !!jwtToken && isLoged.loged;
}

async function getUsers() {
    try {
        const headers = new Headers();
        headers.append("Accept", "application/json");

        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
        };

        const response = await fetch(baseUrl + "users", requestConfig);

        if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
            const json = await response.json();
            if (Array.isArray(json)) {
                users = json;
            } else {
                throw new Error(json + " is not an array.");
            }
        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In getUsers: " + err);
    }
}

/**
 * Met à jour le nombre d'utilisateurs de l'API sur la vue "index".
 */
async function getNumberOfUsers() {
    try {
        await getUsers();
        document.getElementById("nbUsers").innerText = users.length;
    } catch (err) {
        console.error("In getNumberOfUsers: " + err);
    }
}

/**
 * Envoie la requête de login en fonction du contenu des champs de l'interface.
 */
async function connect() {
    const headers = new Headers();
    headers.append("Content-Type", "application/json");

    const body = {
        login: document.getElementById("login_input").value,
        password: document.getElementById("password_input").value
    };

    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    try {
        const response = await fetch(baseUrl + "users/login", requestConfig);

        if (response.status === 204) {
            displayRequestResult("Connexion réussie", "alert-success");

            const authorizationHeader = response.headers.get('Authorization');

            if (authorizationHeader) {
                localStorage.setItem('jwt', authorizationHeader);
                isLoged.loged = true;

                connectedUser.login = body.login;

                console.log("In login: Authorization = " + authorizationHeader);

                await getUser(connectedUser.login, connectedUser);
                insertCompiledTemplate(compiledHeaderTemplate, connectedUser, "header-container");

                displayConnected(true);
            } else {
                console.error('Le header "Authorization" est manquant dans la réponse.');
            }

            location.hash = "#index";
        } else {
            displayRequestResult("Connexion refusée ou impossible", "alert-danger");
            throw new Error("Bad response code (" + response.status + ").");
        }
    } catch (err) {
        console.error("In connect: " + err);
    }
}

async function disconnect() {
    isLoged.loged = false;
    connectedUser.login = "";
    connectedUser.name = "";
    connectedUser.assignedTodos = [];

    localStorage.removeItem("jwt");

    location.hash = "#index";
    displayRequestResult("Déconnexion réussie", "alert-success");
    displayConnected(false);
}

function renderTemplate(scriptId, data, targetId) {
    let template = document.getElementById(scriptId).innerHTML;
    document.getElementById(targetId).innerHTML = Mustache.render(template, data);
}

async function precompileTemplate(scriptId) {
    return new Promise((resolve, reject) => {
        const scriptElement = document.getElementById(scriptId);

        if (!scriptElement) {
            reject(new Error(`Script element with ID '${scriptId}' not found.`));
            return;
        }

        const templateSource = scriptElement.innerHTML;
        const templateSpec = Handlebars.precompile(templateSource);
        const compiledTemplate = Handlebars.template(eval(`(${templateSpec})`));

        resolve(compiledTemplate);
    });
}

function insertCompiledTemplate(compiledTemplate, data, targetId) {
    document.getElementById(targetId).innerHTML = compiledTemplate(data);
}

function updateLogedStatus(newStatus) {
    isLoged.loged = newStatus;
    insertCompiledTemplate(compiledMenuTemplate, isLoged, "menu-container");
}


async function getUser(login, userObject) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible un utilisateur.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        }
        const response = await fetch(baseUrl + "users/" + login, requestConfig)

        if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
            const json = await response.json();

            userObject.login = json.login;
            userObject.name = json.name;
            userObject.assignedTodos = json.assignedTodos;

            return userObject;
        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In getUser : " + err);
    }
}

async function setUsername(userId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de changer son nom.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            name: document.getElementById('nom_update_input').innerText
        };

        const requestConfig = {
            method: "PUT",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "users/" + userId, requestConfig);

        if (response.status === 204) {
            displayRequestResult("Modification du nom réussis", "alert-success");

            await getUser(connectedUser.login, connectedUser);
            insertCompiledTemplate(compiledHeaderTemplate, connectedUser, "header-container");
            displayConnected(true);

        } else {
            displayRequestResult("Erreur lors de la modification du nom ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In setUsername(): " + err);
    }
}

async function setPassword(userId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de changer son mot de passe.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            password: document.getElementById('password_update_input').value
        };

        const requestConfig = {
            method: "PUT",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "users/" + userId, requestConfig);

        if (response.status === 204) {
            displayRequestResult("Modification du mot de passe réussis", "alert-success");
            location.hash = "#monCompte";
        } else {
            displayRequestResult("Erreur lors de la modification du mot de passe ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In setPassword(): " + err);
    }
}

async function getConnectedUser () {
    try {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "text/html");
        headers.append("Authorization", localStorage.getItem('jwt'));
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        }
        await fetch(baseUrl + "users/" + connectedUser.login, requestConfig)
            .then((response) => {
                if (response.ok && response.status === 200 && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                }
            }).then((data) => {
                if (Array.isArray(data)) {
                    console.log(data);
                }
            });
    }
    catch (err) {
        console.log("In getConnectedUser", err);
    }
}

async function createTodo() {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de changer son mot de passe.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            title: document.getElementById('text').value,
            creator: connectedUser.login
        };

        const requestConfig = {
            method: "POST",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos", requestConfig);

        if (response.status === 201) {
            displayRequestResult("Todo crée !", "alert-success");
            await createTodosList();
            insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");
        } else {
            displayRequestResult("Erreur lors de la création d'un todo ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In createTodo(): " + err);
    }
}


async function deleteTodo(todoId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible supprimer ce todo.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const requestConfig = {
            method: "DELETE",
            headers: headers,
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/" + todoId, requestConfig);

        if (response.status === 204) {
            todoArray = [];
            await createTodosList();
            await getNumberOfTodos();
            insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");

        } else {
            displayRequestResult("Erreur lors de la suppression d'un todo ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In deleteTodo: " + err);
    }
}



let todoArray = [];


/**
 *
 * @param todoId l'id tu todo demander.
 * @returns {Promise<any>}
 */
async function fetchTodo(todoId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de récupérer les todos.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/" + todoId, requestConfig);

        if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
            const json = await response.json();


            const isTodoAlreadyPresent = todoArray.some(todo => todo.hash === json.hash);

            if (!isTodoAlreadyPresent) {
                todoArray.push(json);
            }

        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In fetchTodo : " + err);
    }
}

async function fetchAssignedTodo(todoId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de récupérer les todos.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/" + todoId, requestConfig);

        if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
            return await response.json();

        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In fetchTodo : " + err);
    }
}


async function getAssignedTodos() {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de récupérer les todos.");
            return;
        }

        let assignedTodos = [];
        userTodos = [];
        for (const todoId of connectedUser.assignedTodos) {
            const todo = await fetchAssignedTodo(todoId).then(data => {assignedTodos = data; return assignedTodos}) ;
            userTodos.push(todo);
        }

        // Stocker les todos dans une variable ou faire ce que vous voulez avec eux
        console.log(assignedTodos);

        // Vous pouvez également retourner le tableau d'objets si nécessaire
        return assignedTodos;
    } catch (err) {
        console.error("In getAssignedTodos: " + err);
    }
}

let todosIds;

let todos = {
    todosList: [],
    nbTodos : 0
};

/**
 * Récupère tous les todos.
 */
async function fetchTodosIds() {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de récupérer les todos.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos", requestConfig);

        if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
            const json = await response.json();

            const uniqueTodosIds = [...new Set(json)];

            todosIds = uniqueTodosIds;
        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In fetchTodosIds: " + err);
    }
}

async function createTodosList() {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de récupérer les todos.");
            return;
        }
        await fetchTodosIds();
        const todoPromises = todosIds.map(todoId => fetchTodo(todoId));
        const resolvedTodos = await Promise.all(todoPromises);

        todos.todosList = todoArray;

        todos.todosList.forEach(function(todo) {
            let assignee = todo.assignee ?? "";
            todo.isAssignee = assignee === connectedUser.login;
        });

        todos.nbTodos = todos.todosList.length;
    } catch (err) {
        console.error("In createTodosList: " + err);
    }
}

async function updateAssigned(todoId, login) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de modifier l'utilisateur assignée à ce todo.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            assignee: login
        };

        const requestConfig = {
            method: "PUT",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/" + todoId, requestConfig);

        if (response.status === 204) {
            displayRequestResult("Modification de l'utilisateur assignée réussis", "alert-success");
            todoArray = [];
            await createTodosList();
            insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");

        } else {
            displayRequestResult("Erreur lors de la modification de l'utilisateur assignée ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In updateAssigned(): " + err);
    }
}

async function updateTodoStatus(todoId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de modifier le status de ce todo.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            hash : todoId
        };

        const requestConfig = {
            method: "POST",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/toggleStatus", requestConfig);

        if (response.status === 204) {
            todoArray = [];
            await createTodosList();
            insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");

        } else {
            displayRequestResult("Erreur vous n'êtes pas authorizer à modifier ce todo", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In updateTodoStatus: " + err);
    }
}

async function updateTodoTitle(todoId) {
    try {
        if (!isConnected()) {
            console.error("L'utilisateur n'est pas connecté. Impossible de modifier le status de ce todo.");
            return;
        }

        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "application/json");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const body = {
            title : document.getElementById('todo-title').innerText
        };

        const requestConfig = {
            method: "PUT",
            headers: headers,
            body: JSON.stringify(body),
            mode: "cors"
        };

        const response = await fetch(baseUrl + "todos/" + todoId, requestConfig);

        if (response.status === 204) {
            todoArray = [];
            await createTodosList();
            insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");

        } else {
            displayRequestResult("Erreur vous n'êtes pas authorizer à modifier ce todo", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In updateTodoStatus: " + err);
    }
}

async function getNumberOfTodos() {
    if (!isConnected()) {
        return;
    }

    try {

        await fetchTodosIds();

        const numberOfTodos = todosIds.length;

        document.getElementById("nbTodos").innerText = numberOfTodos;
    } catch (err) {
        console.error("In getNumberOfTodos: " + err);
    }
}

async function updateTodosList() {
    if (!isConnected()) {
        console.error("L'utilisateur n'est pas connecté. Impossible de modifier le status de ce todo.");
        return;
    }

    todoArray = [];
    await createTodosList();
    insertCompiledTemplate(compiledTodosTemplate, todos, "todos-container");
}

setInterval(getNumberOfUsers, 5000);

setInterval(getNumberOfTodos, 10000);
setInterval(updateTodosList, 5000);


