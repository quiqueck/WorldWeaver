export function toCamelCase(str){
  return str.split(' ').map(function(word,index){
    // If it is not the first word only upper case the first char and lowercase the rest.
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  }).join('');
}