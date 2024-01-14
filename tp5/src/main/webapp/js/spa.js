/**
 * Placez ici les scripts qui seront exécutés côté client pour rendre l'application côté client fonctionnelle.
 */

// Partie 2.2 Mock Object

    // User

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

const user2 = {
    login: "toto",
    name: "Toto",
    assignedTodos: null
}


let users = []

let user = {
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

function getUserName() {
    return fetch(baseUrl + "users/name")
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erreur de l'API: ${response.status}`);
            }
            return response.json();
        })
        .then(data => data.name)
        .catch(error => {
            console.error('Erreur lors de la récupération du nom d\'utilisateur depuis l\'API', error);
            throw error;
        });
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
            displayConnected(true);

            const authorizationHeader = response.headers.get('Authorization');

            if (authorizationHeader) {
                localStorage.setItem('jwt', authorizationHeader);
                user.login = body.login;
                // Todo : Appellez un fonction qui vas get le user et update l'obj user
                console.log("In login: Authorization = " + authorizationHeader);
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
    user.login = "";
    user.name = "";
    user.assignedTodos = [];

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

function insertCompiledTemplate(compiledTemplate,data, targetId) {
    document.getElementById(targetId).innerHTML = compiledTemplate(data);
}

function updateLogedStatus(newStatus) {
    isLoged.loged = newStatus;
    insertCompiledTemplate(compiledMenuTemplate, isLoged, "menu-container");
}

// Todo add function for re-render (insert) template when the name is modifie

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
            user.name = body.name;
            // Todo déclacher un mise à jour
            location.hash = "#monCompte";
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
            user.name = body.name;
            location.hash = "#monCompte";
        } else {
            displayRequestResult("Erreur lors de la modification du mot de passe ", "alert-danger");
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In setPassword(): " + err);
    }
}


let todoStamp;

/**
 *
 * @param todoId l'id tu todo demander.
 * @returns {Promise<any>}
 */
async function getTodo(todoId) {
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
            todoStamp = json;

        } else {
            throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
        }
    } catch (err) {
        console.error("In getTodo : " + err);
    }
}

let todosIds;

let todos = {
    "todosList": [],
    "nbTodos" : 0
};


async function addTodos(todosIds) {
    try {
        const todoPromises = todosIds.map(todoId => getTodo(todoId));
        const resolvedTodos = await Promise.all(todoPromises);

        const uniqueTodos = resolvedTodos.filter(newTodo => !todos.todosList.some(existingTodo => existingTodo.hash === newTodo.hash));

        todos.todosList = todos.todosList.concat(uniqueTodos);

        todos.nbTodos = todos.todosList.length;

        console.log("Tableau fini : ", todos);
    } catch (err) {
        console.error("In addTodos: " + err);
    }
}


/**
 * Récupère tous les todos.
 */
async function getTodos() {
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
        console.error("In getTodos: " + err);
    }
}

async function getNumberOfTodos() {
    try {
        await getTodos();

        const numberOfTodos = todos.todosList.length;

        document.getElementById("nbTodos").innerText = numberOfTodos;
    } catch (err) {
        console.error("In getNumberOfTodos: " + err);
    }
}

setInterval(getNumberOfUsers, 5000);

setInterval(getNumberOfTodos, 10000);
setInterval(getTodos, 5000);


