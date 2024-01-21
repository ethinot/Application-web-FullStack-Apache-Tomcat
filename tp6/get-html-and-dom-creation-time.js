/* Source
- https://github.com/addyosmani/timing.js/blob/master/timing.js
- https://w3c.github.io/perf-timing-primer/ 5.2 Intéressant
- https://www.w3.org/TR/navigation-timing-2/#dom-performancetiming-requeststart
*/

// Temps de la requêtes HTTP
function getHTTPRequestTime() {
    if (window.performance && window.performance.timing) {
        const timing = window.performance.timing;

        if ('requestStart' in timing && 'responseEnd' in timing) {
            const getHTTPTime = timing.responseEnd - timing.requestStart;
            
            console.log("Temps de recuperation de la page HTML : ", getHTTPTime, " ms");

        } else {
            console.log("Les proprietes requestStart et responseEnd n'existent pas");
        } 
    } else {
        console.log("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
    }
} 

// Temps de création du DOM depuis le début de la première requêtte HTTP
function getDomCreationTime() {
  if (window.performance && window.performance.timing && window.performance.getEntriesByType) {
      const navigationEntries = performance.getEntriesByType('navigation');

      if (navigationEntries.length > 0) {
          const navigationEntry = navigationEntries[0];

          const requestStartTime = performance.timing.requestStart;

          const domCreationTime = (navigationEntry.domContentLoadedEventEnd - requestStartTime) + performance.timeOrigin;

          console.log("Temps de création du DOM depuis la première requêtte HTTP :", domCreationTime, "ms");
          
          return domCreationTime;
         
      } else {
          console.error("Aucune entrée de navigation disponible.");
      }
  } else {
      console.error("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
  }
  return null;
}

// Temps de chargement des données nécessaires à la création du CSSOM  
function getCSSOMLoadTime() {
  if (window.performance && window.performance.getEntriesByType) {
      const resourceEntries = performance.getEntriesByType('resource');
      const filteredCssEntries = resourceEntries.filter(entry => {
          return entry.initiatorType === 'link' &&
                 entry.entryType === 'resource' &&
                 !entry.name.includes('tarteaucitron') &&
                 !entry.name.includes('youtube') &&
                 !entry.name.includes('64ea71f5e0cf3199e1a65da7e62732c54d9e5def'); // CSS apèrs le CRP
      });

      if (filteredCssEntries.length > 0) {
          const maxCssEntry = filteredCssEntries.reduce((maxEntry, entry) => {
              return entry.duration > maxEntry.duration ? entry : maxEntry;
          }, filteredCssEntries[0]);

          const requestStartTime = performance.timing.requestStart;
          const cssomLoadTime = (maxCssEntry.responseEnd - requestStartTime) + performance.timeOrigin;

          console.log("Temps de chargement du CSSOM :", cssomLoadTime, " ms");
          
          return cssomLoadTime;
      } else {
          console.log("Aucune entrée CSS trouvée dans le filter.");
      }
  } else {
    console.error("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
  }
  return null;
}

// Le temps de chargement de l'app shell. Ce temps est éstimé car il se base sur la dernière ressource supposément charger pour l'app shell.
function getAppShellLoadTime() {
  // Nous pouvons prendre comme ressources l'image qui suit comme dernière ressource chargée dans l'app shell.
  const imageUrl = 'https://www.univ-lyon1.fr/uas/WWW-2014/LOGO/LogoUCBL1.png';

  if (window.performance && window.performance.getEntriesByType) {
      const resourceEntries = performance.getEntriesByType('resource');
      const filteredImageEntries = resourceEntries.filter(entry => {
          return entry.initiatorType === 'img' &&
                 entry.entryType === 'resource' &&
                 entry.name === imageUrl;
      });

      if (filteredImageEntries.length > 0) {
          const imageEntry = filteredImageEntries[0];

          const requestStartTime = performance.timing.requestStart;
          const appShellLoadTime = (imageEntry.responseEnd - requestStartTime) + performance.timeOrigin;

          console.log("L'estimation du temps de chargement de l'app shell ;", appShellLoadTime, " ms");

          return appShellLoadTime;
      } else {
          console.log("La ressource n'a pas été trouvée !");
      }
  } else {
    console.error("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
  }
  return null;
}

// Le temps de chargement du CRP. Ce temps est éstimé car il se base sur la dernière ressource supposément charger pour le CRP.
function getCRPLoadTime() {
  // Idem que pour l'app shell, on prend cette fois ci le script comme dernière ressource chargée du CRP.
  const scriptUrl = 'https://www.univ-lyon1.fr/jspWWW/scripts/tarteaucitron.1.5/tarteaucitron.services.js?v=20200730';

  if (window.performance && window.performance.getEntriesByType) {
      const resourceEntries = performance.getEntriesByType('resource');
      const filteredScriptEntries = resourceEntries.filter(entry => {
          return entry.initiatorType === 'script' &&
                 entry.entryType === 'resource' &&
                 entry.name === scriptUrl;
      });

      if (filteredScriptEntries.length > 0) {
          const scriptEntry = filteredScriptEntries[0];

          const requestStartTime = performance.timing.requestStart;
          const CRPLoadTime = (scriptEntry.responseEnd - requestStartTime) + performance.timeOrigin;

          console.log("L'estimation du temps de chargement du CRP ;", CRPLoadTime , " ms");

          return CRPLoadTime;
      } else {
        console.log("La ressource n'a pas été trouvée !");
      }
  } else {
    console.error("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
  }
  return null;
}

getHTTPRequestTime()
getDomCreationTime()
getCSSOMLoadTime()
getAppShellLoadTime()
getCRPLoadTime()