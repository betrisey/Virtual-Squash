![virtualsquash](https://cloud.githubusercontent.com/assets/1939395/11165388/a51cdcfa-8b0d-11e5-9d7c-415915b4c658.png)

# 1 Documentation

## 1.1 Organisation du code

### 1.1.1 Classe Main
Classe de la fenêtre principale. Elle fait le lien entre la Kinect et les classes Player, Ball et Game. En cas d'utilisation d'un autre appareil, il y aura seulement cette classe à modifier.
- Mets à jour les positions des joueurs (classe _Player_)
- Instancie les classes _Ball_ et _Game_
- Affiche l'interface graphique (scores, vainqueur, bouton recommencer)

### 1.1.2 Classe Player
- Stocke les positions du joueur et son score
- Affiche la raquette
- Calcule la direction de la frappe (détaillé dans Simulation des rebonds – raquette)

### 1.1.3 Classe Balle
Permet d'afficher une balle qui rebondit contre les murs.
On peut la faire rebondir dans une direction avec la méthode _bounce_ et la faire accélérer avec la méthode _accelerate_.
On peut changer sa couleur (attribut _color_).

### 1.1.4 Classe Game
- Gère les tours des joueurs
- Calcule des scores
- Vérifie si le joueur frappe la balle et fait bouger la balle en conséquence
- Désigne le vainqueur et si la partie est finie
- Choisi le mode de jeu en fonction du nombre de joueurs détectés

## 1.2 Fonctionnalités

### 1.2.1 Multi-Joueurs

- 1 joueur : Gagne 1 point à chaque rebond de la balle. S'il n'arrive pas à la renvoyer assez vite (voir timeout) son score est remis à zéro. Son meilleur score est affiché.
- 2 joueurs : Les joueurs font rebondir la balle l'un après l'autre. Si un joueur n'arrive pas à la frapper à son tour, son adversaire marque 1 point. La partie se termine à 21 points avec 2 points d'écart.
- Plus de 2 joueurs : Ce sont les mêmes règles que pour 2 joueurs. Pour activer ce mode, il faudra augmenter la constante limite de joueurs (NOMBRE\_JOUEURS).

Le jeu passe automatiquement d'un mode à l'autre en fonction du nombre de joueurs détectés.

### 1.2.2 Simulation des rebonds – murs et fond

Lorsque la balle touche un bord de l'écran ou le fond de la salle, elle rebondit dans la direction inverse (Par exemple, si elle atteint la position maximale sur l'axe _z_, son vecteur de déplacement _z_ est inversé).

C'est la seule partie pour laquelle j'ai gardé pas mal de code du projet fait il y a 2 ans.

### 1.2.3 Simulation des rebonds – raquette

Lorsque le joueur frappe la balle, elle part dans la direction du mouvement de sa main.

Pour donner un effet de rebond plus réaliste, j'ai calculé la direction de la frappe avec les 15 dernières positions, normalisé ce vecteur et multiplié par la norme du vecteur du déplacement de la balle pour conserver la vitesse de la balle.

Donc la balle prend la direction de la frappe tout en conservant sa vitesse.

### 1.2.4 Taille de la raquette

La raquette a une taille de base (définie par les constantes WIDTH et HEIGHT) ensuite elle varie lorsque la main s'approche ou s'éloigne de la Kinect.

### 1.2.5 Gaucher ou droitier

Au début de la partie, le joueur met en avant la main qu'il veut utiliser. La raquette sera dans cette main tout au long de la partie.

### 1.2.6 Couleur des raquettes et de la balle

Chaque joueur a une raquette de couleur différente. Lorsque c'est à son tour de frapper la balle, cette dernière devient de la même couleur que la balle.

### 1.2.7 Bouton recommencer

A la fin de la partie, un bouton recommencer est affiché et le joueur peut facilement le sélectionner avec sa main au lieu de devoir appuyer sur une touche du clavier.

### 1.2.8 Affichage de la raquette

L'inclinaison du bras est reproduite par la raquette dans le jeu.

## 1.3 Réglages de constantes possibles

### 1.3.1 LONGUEUR\_ECRAN et LARGEUR\_ECRAN (Main.java)

Résolution de l'écran

### 1.3.2 NOMBRE\_JOUEURS (Main.java)

Permet de limiter le nombre de joueurs

Modes de jeu selon Multi-Joueurs

### 1.3.3 FACTEUR\_HORIZONTAL et FACTEUR\_VERTICAL (Main.java)

Utilisés lors de la conversion des données de la Kinect (mètres  pixels).

Pour que la main soit alignée à la raquette virtuelle, il faut faire varier ces valeurs qui dépendent de la taille de l'écran et de la distance du joueur.

### 1.3.4 TIMEOUT (Game.java)

Temps que le joueur a pour frapper la balle lorsqu'elle arrive vers lui.

### 1.3.5 WIDTH et HEIGHT (Player.java)

Taille de la raquette à 1 mètre. Elle variera automatiquement si la main se rapproche ou s'éloigne.

## 1.4 Installation du projet

Installer les SDK Kinect (2.0+) [https://dev.windows.com/en-us/kinect](https://dev.windows.com/en-us/kinect)

Installer Java JDK 8+ [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### 1.4.1 IntelliJ

Le projet et ses dépendances sont automatiquement détectés.

![picture1](https://cloud.githubusercontent.com/assets/1939395/11165344/61de7b7a-8b0c-11e5-955e-780c41a51dc1.png)
![picture2](https://cloud.githubusercontent.com/assets/1939395/11165346/61e12546-8b0c-11e5-8961-03e45347aed7.png)

En cas de problème, vérifiez que ces librairies soient bien chargées.

![picture3](https://cloud.githubusercontent.com/assets/1939395/11165348/61e269ce-8b0c-11e5-9275-7e785691ff69.png)

### 1.4.2 Eclipse

Le projet a été développé avec IntelliJ mais j'ai créé les fichiers nécessaires pour qu'il soit importable directement dans Eclipse.

![picture4](https://cloud.githubusercontent.com/assets/1939395/11165349/61e33818-8b0c-11e5-9eb6-a54ca62e5b45.png)
![picture5](https://cloud.githubusercontent.com/assets/1939395/11165347/61e25cfe-8b0c-11e5-8977-4c8f65396fcc.png)
![picture6](https://cloud.githubusercontent.com/assets/1939395/11165345/61dfe956-8b0c-11e5-80be-314e5cf13fe8.png)

En cas de problème, vérifiez que ces librairies soient bien chargées.

![picture7](https://cloud.githubusercontent.com/assets/1939395/11165350/61f606aa-8b0c-11e5-9750-abb72f755e4b.png)

# 2 Améliorations possibles

## 2.1 Multi-joueurs en réseau

Un mode multi-joueurs en réseau serait tout à fait réalisable avec quelques jours de plus et une deuxième Kinect.

Connexion socket entre 2 PCs :
- Le PC « serveur » calcule les rebonds, scores et un joueur joue sur celui-ci
- Le PC « client » envoie la position de son joueur au serveur (objet Player sérialisé) et récupère la position de la balle, le score etc…

## 2.2 Utilisation d'un moteur 3D et physique

Pour une simulation plus réaliste des rebonds, de la gravité et de la profondeur

Par exemple avec Unity ( [https://unity3d.com/](https://unity3d.com/)) ou Unreal Engine ( [https://www.unrealengine.com/](https://www.unrealengine.com/)), tous deux gratuits pour notre type d'usage.
