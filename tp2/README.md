# Notes TP2 

## 1) Configuration d'un serveur HTTP

### 1.1) Prise en main d'nginx

#### Q1) Quel est l'utilisateur qui a lancé le serveur ? Pourquoi ?
Grâce à la commande ``` ps -aux ``` on constate que c'est l'utilisateur root qui à lancer le processusNginx. C'est l'utilisateur root qui lance les processus car c'est le seul à pouvoir écouter sur des ports en dessous de 1024. 

#### Q2) Quel est l'utilisateur qui fait tourner les "workers" ? Pourquoi ?

C'est l'utilisateur qui s'appelle nginx qui lance les workers, cela permets entre autre de limiter que des personnes malveillants qui prendrais possession d'un worker ai les accès root.

#### Q3) Que signifie la deuxième ligne (non vide) du fichier nginx.conf et comment vérifier qu'elle est bien prise en compte ?

La deuxième ligne non vide permet de définir le nombre de worker process en route simultanément. La mention auto detecte le nombre de coeurs présent sur la machine hôte et adapte le nombre de workers en fonction.

Pour vérifier cela on modifie la valeur auto par 2 par exemple. Avec la commande ps aux on se rend bien compte que le nombre de workers nginx est de deux.


#### Q4) Où est situé le fichier de configuration du site actuellement déployé et comment le serveur le "trouve"-t-il ?

Le fichier de configuation actuel est dans le dosier /etc/nginx/conf.d/default.conf. Le serveur le trouve grâce à la dernier ligne du fichier /etc/nginx/nginx.conf

```
include /etc/nginx/conf.d/*.conf;
```

#### Q5) Dans ce fichier, comment est spécifié le port sur lequel répond le serveur (bloc et directive) ?

La directive "listen" du bloc "server" permet de spécifier le port d'écoute du serveur nginx, ici le port 80. 

#### Q6) Quel est le répertoire où se trouvent les données du site (fichier et directive) ? Pour vérifier, modifiez la page d'accueil de ce site, et constatez le résultat dans le navigateur.

Le repertoire /usr/share/nginx/html/ contient les données du site (dont le index.html)

```
location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }
```

Dans le fichier /etc/nginx/conf.d/default.conf la directive "root" du bloc "location" indique le répertoire des données du site.

### 1.2) Configuration d'un site Web existant

#### Q7) Indiquez le nom du fichier de configuration que vous avez modifié et le contenu que vous avez modifié.

* Fichier de configuration modifier : /etc/nginx/conf.d/default.conf 

* Les modification apporter au fichier :
	* ```
	location / {
            root   /usr/share/nginx/default;
            index  index.html index.htm;
        }	
	```
	* ```
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

### 1.3) Mise en place d'un nouveau site

#### Q9) Indiquez l'erreur renvoyée par le serveur et la raison pour laquelle elle est présente.

L'erreur renvoyée par le serveur est 404 Not Found, la raison de cette erreur est que le répertoire qui contient les données du site (html etc..) n'est pas présent (/usr/share/nginx/monsite/).

### 1.4) Scripting côté serveur

#### Q10) Décrivez et expliquez - côté serveur et côté client - ce qui se passe et la raison pour laquelle vous obtenez ce comportement.

Côté client, le fichier n'est pas interpréter et ainsi telecharger.

Côté serveur, la configuration du serveur (default) ne prend pas en charge les requettes de fichier PHP. 

On obient ce comportement car c'est le comportement par défaut qui est de télécharger les fichier inconnue. (Vraiment pas dingue)

