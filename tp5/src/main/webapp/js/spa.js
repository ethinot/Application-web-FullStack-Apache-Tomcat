/**
 * Placez ici les scripts qui seront exécutés côté client pour rendre l'application côté client fonctionnelle.
 */

// Partie 2.2 Mock Object

    // User

const users = [
    "users/toto",
    "users/titi"
]


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

let userConnected;

let isLoged = {
    "loged": false
}




    // Todo

const todos = [todo1, todo2, todo3];



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
    for(const element of elementsRequiringConnection) {
        element.style.visibility = visibilityValue;
    }
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
    return !!jwtToken;
}

/**
 * Met à jour le nombre d'utilisateurs de l'API sur la vue "index".
 */
function getNumberOfUsers() {
    const headers = new Headers();
    headers.append("Accept", "application/json");
    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "users", requestConfig)
        .then((response) => {
            if(response.ok && response.headers.get("Content-Type").includes("application/json")) {
                return response.json();
            } else {
                throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
            }
        }).then((json) => {
            if(Array.isArray(json)) {
                document.getElementById("nbUsers").innerText = json.length;
            } else {
                throw new Error(json + " is not an array.");
            }
        }).catch((err) => {
            console.error("In getNumberOfUsers: " + err);
        });
}

/**
 * Met à jour le nombre de todos créer sur la vue "todoList".
 */
function getNumberOfTodos() {
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
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "todos", requestConfig)
        .then((response) => {
            if(response.ok && response.headers.get("Content-Type").includes("application/json")) {
                return response.json();
            } else {
                throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
            }
        }).then((json) => {
        if(Array.isArray(json)) {
            document.getElementById("nbTodos").innerText = json.length;
        } else {
            throw new Error(json + " is not an array.");
        }
    }).catch((err) => {
        console.error("In getNumberOfUsers: " + err);
    });
}

/**
 * Envoie la requête de login en fonction du contenu des champs de l'interface.
 */
function connect() {
    displayConnected(true);
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
    fetch(baseUrl + "users/login", requestConfig)
        .then(async (response) => {
            if (response.status === 204) {
                displayRequestResult("Connexion réussie", "alert-success");
                const authorizationHeader = response.headers.get('Authorization');
                if (authorizationHeader) {
                    localStorage.setItem('jwt', authorizationHeader);
                    console.log("In login: Authorization = " + authorizationHeader);
                } else {
                    console.error('Le header "Authorization" est manquant dans la réponse.');
                }
                location.hash = "#index";
            } else {
                displayRequestResult("Connexion refusée ou impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In login: " + err);
        })
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
    renderTemplate('menu-template', isLoged, 'menu-container');
}

function deco() {
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", localStorage.getItem('jwt'));
    const requestConfig = {
        method : "POST",
        headers : headers
    };
    fetch(baseUrl + "users/logout", requestConfig)
        .then((response) => {
            if (response.status === 204){
                displayRequestResult("Déconnexion réussie", "alert-success");
                location.hash = "#index";
            } else {
                displayRequestResult("Déconnexion échouée", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In logout " + err);
        })
    displayConnected(false);
}

async function getAllUsers () {
    try {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        const reqConfig = {
            method: "GET",
            headers: headers
        };
        await fetch(baseUrl + "users", reqConfig)
            .then((response) => {
                if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                }
            })
            .then((data) => {
                if (Array.isArray(data)) {
                    console.log(data);
                }
            });
    } catch (err) {
        console.error("In getAllUsers", err);
    }
}

async function getConnectedUser () {
    try {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "text/html");
        headers.append("Authorization", `${tokenJWT}`);
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        }
        await fetch(baseUrl + "users/" + userLogin, requestConfig)
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

async function getUserName() {
    try {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Content-Type", "text/html");
        headers.append("Authorization", localStorage.getItem('jwt'));

        const requestConfig = {
            method : "GET",
            headers : headers
        }

        await fetch(baseUrl + users[0] + "/name", requestConfig)
            .then((response) => {
                if(response.ok && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                }
            }).then((data) => {
                userConnected = data;
                return data;
            })
    } catch (e) {
        console.log("In getUserName", e);
    }
}


setInterval(getNumberOfUsers, 5000);
setInterval(getNumberOfTodos, 10000);
