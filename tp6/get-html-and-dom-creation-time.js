/* Source
- https://github.com/addyosmani/timing.js/blob/master/timing.js
- https://w3c.github.io/perf-timing-primer/ 5.2 Intéressant
- https://www.w3.org/TR/navigation-timing-2/#dom-performancetiming-requeststart
*/

//Request + Response HTTP
// responseEnd - requestStart
// dépréciée ?
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

// DOMContentLoaded de l'interface PerformanceNavigationTiming 
// Soit domContentLoadedEventEnd - domContentLoadedEventStart
// Soit domComplete
// Soit domContentLoadedEventEnd - startTime
if (window.performance && window.performance.getEntriesByType) {
const navigationEntries = performance.getEntriesByType('navigation');

if (navigationEntries.length > 0) {
    // Récupérez la première entrée de navigation (la plus récente)
    const navigationEntry = navigationEntries[0];

    if ('domComplete' in navigationEntry) {
    const domCompleteTime = navigationEntry.domComplete;

    console.log("Temps de complétion du DOM :" , domCompleteTime, " ms");
    } else {
    console.log("L'attribut domComplete n'existe pas.");
    }
} else {
    console.log("Aucune entree de navigation disponible.");
}
} else {
console.log("L'API de navigation timing ne fonctionne pas sur ce navigateur.");
}