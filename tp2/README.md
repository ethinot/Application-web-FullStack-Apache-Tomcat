# Notes TP2 

## 1) Configuration d'un serveur HTTP

### 1.1) Prise en main d'nginx

#### Q1) Quel est l'utilisateur qui a lancé le serveur ? Pourquoi ?
Grâce à la commande ``` ps -aux ``` on constate que c'est l'utilisateur root qui a lancé le processus Nginx. C'est l'utilisateur root qui lance les processus, car c'est le seul à pouvoir écouter sur des ports en dessous de 1024. 

#### Q2) Quel est l'utilisateur qui fait tourner les "workers" ? Pourquoi ?

C'est l'utilisateur qui s'appelle nginx qui lance les workers, cela permets entre autres de limiter que des personnes malveillants qui prendrais possession d'un worker ai les accès root.

#### Q3) Que signifie la deuxième ligne (non vide) du fichier nginx.conf et comment vérifier qu'elle est bien prise en compte ?

La deuxième ligne non-vide permet de définir le nombre de worker process en route simultanément. La mention auto detecte le nombre de cœurs présent sur la machine hôte et adapte le nombre de workers en fonction.

Pour vérifier cela, on modifie la valeur auto par 2 par exemple. Avec la commande "ps aux" on se rend bien compte que le nombre de workers nginx est de deux.


#### Q4) Où est situé le fichier de configuration du site actuellement déployé et comment le serveur le "trouve"-t-il ?

Le fichier de configuration actuel est dans le dossier /etc/nginx/conf.d/default.conf. 
Le serveur le trouve grâce à la dernière ligne du fichier /etc/nginx/nginx.conf

```
include /etc/nginx/conf.d/*.conf;
```

#### Q5) Dans ce fichier, comment est spécifié le port sur lequel répond le serveur (bloc et directive) ?

La directive "listen" du bloc "server" permet de spécifier le port d'écoute du serveur nginx, ici le port 80. 

#### Q6) Quel est le répertoire où se trouvent les données du site (fichier et directive) ? Pour vérifier, modifiez la page d'accueil de ce site, et constatez le résultat dans le navigateur.

Le répertoire /usr/share/nginx/html/ contient les données du site (dont l'index.html)

```
location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }
```

Dans le fichier /etc/nginx/conf.d/default.conf la directive "root" du bloc "location" indique le répertoire des données du site.

---

### 1.2) Configuration d'un site Web existant

#### Q7) Indiquez le nom du fichier de configuration que vous avez modifié et le contenu que vous avez modifié.

* Fichier de configuration modifié : `/etc/nginx/conf.d/default.conf`

* Les modifications apportées au fichier :
	```
	location / {
            root   /usr/share/nginx/default;
            index  index.html index.htm;
        }	
	```
	```
	location = /50x.html {
            root   /usr/share/nginx/default;
        }
	```

#### Q8) Copiez-collez les modifications apportées au fichier de configuration.

```
location /images/ {
        alias /usr/share/nginx/images/;
}
```

---

### 1.3) Mise en place d'un nouveau site

#### Q9) Indiquez l'erreur renvoyée par le serveur et la raison pour laquelle elle est présente.

L'erreur renvoyée par le serveur est 404 Not Found, la raison de cette erreur est que le répertoire qui contient les données du site (html etc..) n'est pas présent (/usr/share/nginx/monsite/).

---

### 1.4) Scripting côté serveur

#### Q10) Décrivez et expliquez - côté serveur et côté client - ce qui se passe et la raison pour laquelle vous obtenez ce comportement.

Côté client, le fichier n'est pas interprétés et ainsi télécharger.

Côté serveur, la configuration du serveur (default) ne prend pas en charge les requêtes de fichier PHP. 

On obtient ce comportement, car c'est le comportement par défaut qui est de télécharger les fichiers inconnues.

## 2) Mise en place de l'infrastructure des TPs suivants

### 2.1) Sécurisation du serveur

#### 2.1.1) Téléchargement et installation des certificats et des clés pré-générées

* Ajout des certificats (clé/certificat) dans le dossier /etc/ssl/(private/cert)

* Versionning des certifications sur la forge dans le fichier tp2/2.2.-securisation 

#### 2.1.2) Analyse du certificat

#### Q11) Qui est l’émetteur du certificat (issuer) ?

L'émetteur du certificat est "M1IF-CA".

#### Q12) Qui est le sujet certifié (subject) ?

Le sujet certifié est "m1if03-2023-2024-C04".

#### Q13) Quelle est la durée de validité de votre certificat ?

La durée de validité du certificat est de 365 jours (un an). Elle ne dépassera pas la 25 septembre 10 heures, 41 minutes et 51 secondes.

---

### 2.1.3) Mise en place du HTTPS 

#### Q14) Quelle est la réaction de votre navigateur ?

Notre navigateur prévient que le site est non sécurisé et tente de dissuader l'utilisateur d'y accéder.

#### Q15) Que veut dire le cadenas avec le point d'exclamation ?

Le cadenas symbolise que le certificat est non valide. Cela survient lorsque le certificat n'est pas présent dans le magasin.

#### Q16) Que se passe-t-il ?

Le point d'exclamation sur le cadenas disparaît. Cela signifie que la certification est bien validée pour le navigateur suite à l'ajout de ce dernier.

---

### 2.1.4) Redirection HTTP

#### Q17) Quelle est la différence entre les codes de redirection 301 et 308 ?

* "Moved Permanently" 301 : permet le changement de la méthode de la requette lors de la rediraction

* "Permanent Redirect" 308 : ne permet pas le changement de la méthode (GET/POST) lors de la redirection

## 2.2) Front server / reverse proxy pour Tomcat

#### Q18) Indiquez le bloc et les directives que vous avez rajoutés dans le fichier https.conf.

Voici les deux blocs comportant les directives de redirection :

```
location /api/ {
    proxy_pass http://localhost:8080/;
}

location /manager {
    proxy_pass http://localhost:8080/manager;
}
```

## 2.3) Gestion des sessions et des cookies

#### Q19) Quel est ce mécanisme ? Comment faire pour que le serveur vous "oublie" ?

Les cookies sont à l'origine de la gestion de la session utilisateur. Se mettre en navigation privée ou supprimer les cookies "à la main" sont un moyen pour que le serveur nous "oublie". 

#### Q20) À l'aide de la console réseau du navigateur expliquez ce qui se passe.

Le proxy NGINX ne transmet pas les cookies de la session utilisateur (HTTP 403). Le cookie lié à la requete est refusé pour des raisons de sécurité par le proxy Nginx, il ne le transmet donc pas à Tomcat.

#### Q21) Quelle directive avez-vous rajoutée ?

La directive placée dans la location /api/ ``` proxy_coockie_path / "/; secure; HttpOnly; SameSite=strict" ``` permet de résoudre le problème précédent. 

## Déploiement de votre projet forge

* On ajout l'utilisateur gitlabci au groups tomcat
	* ``` sudo usermod -a -G tomcat gitlabci ```

* Changer les droits sur le repertoire /opt/tomcat/webapps (récursivement) pour que les utilisateur du group tomcat puissent écrire (initalement r-x)
	* ``` sudo chmod -R g+w /opt/tomcat/webapps/ ```



* Ajout d'une variables "CI_SSH_KEY" de type file sur la forge CI/CD qui contient la clé ssh pour l'utilisateur gitlabci (settings -> Variables -> Add variable)

* Ajout du script "setup_mvn_proxy.sh" dans le fichier ci/ de la forge 

* Configuration du fichier de configuation du CI/CD "gitlab-ci.yml" 

* Ajout de deux variables de configuration :
	* CURRENT_TP_FOLDER -> fichier de tp actuel (exemple : "tp1/")
	* DEPLOY_PATH -> chemin de deploiment de l'app dans tomcat (exemple : "/opt/tomcat/webapps/v1.war")
