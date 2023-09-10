import fs from 'fs'
import Path from 'path'
import {toCamelCase} from './util.js'

export const createDefinitions = [
    { name: 'title', type: String, typeLabel: '{underline name}', description: 'The human readable name of the sub module', multiple:false, defaultOption: true },
    { name: 'namespace', alias: 'n', type: String, typeLabel: '{underline name}', description: 'namespace of this sub module. The actual name will get prefixed with "{italic wover-}".' },
    { name: 'main', alias: 'm', type: Boolean, description: 'Should a main mod data get created', defaultOption: false },
    { name: 'client', alias: 'c', type: Boolean, description: 'Should a client mod get created', defaultOption: false },
    { name: 'test', alias: 't', type: Boolean, description: 'Should a test mod get created', defaultOption: false },
    { name: 'clientTest', alias: 'C', type: Boolean, description: 'Should a clientside test mod get created', defaultOption: false },
    { name: 'datagen', alias: 'd', type: Boolean, description: 'Should a datagen entry get created', defaultOption: false },
    { name: 'datagenTest', alias: 'D', type: Boolean, description: 'Should a datagen testmod entry get created', defaultOption: false }
]

export const createSections = [
    {
      header: 'Synopsis - {italic create}',
      content: 'Creates a new sub-module\n$ node wover.js create <options> '
    },
    {
      header: 'Options',
      optionList: createDefinitions
    }
]

export function validateCreateOptions(options) {
    return options.title !== undefined && options.namespace !== undefined;

}

function mkdir(dir){
    if (!fs.existsSync(dir)){
        fs.mkdirSync(dir, { recursive: true });
    }
}
function mksub(base, sub){
    const dir = Path.join(base, sub)
    mkdir(dir);
    return dir;
}

export function create(options){
    const javaName = toCamelCase(options.title)
    const mainClass = `Wover${javaName}`
    const clientClass = `${mainClass}Client`
    const datagenClass = `${mainClass}Datagen`

    const mainTestClass = `Wover${javaName}TestMod`
    const clientTestClass = `${mainClass}ClientTestMod`
    const datagenTestClass = `${mainClass}DatagenTestMod`

    const loadAndReplaceTest = (path, subPackage) => {
        if (subPackage===undefined) subPackage = options.namespace
        else `${options.namespace}.${subPackage}`

        return fs
          .readFileSync(path, 'utf8')
          .replaceAll('{title}', options.title+' (TestMod)')
          .replaceAll('{namespace}',subPackage.replaceAll(/\./g, '-')+'-testmod')
          .replaceAll('{package}', subPackage+'.testmod')
          .replaceAll('{subPackage}', subPackage)
          .replaceAll('{mainClass}', mainTestClass)
          .replaceAll('{clientClass}', clientTestClass)
          .replaceAll('{datagenClass}', datagenTestClass)
          .replaceAll('{postfix}', '.testmod')
    }

    const loadAndReplace = (path, subPackage) => {
        if (subPackage===undefined) subPackage = options.namespace
        else `${options.namespace}.${subPackage}`

        return fs
          .readFileSync(path, 'utf8')
          .replaceAll('{title}', `${options.title} API`)
          .replaceAll('{namespace}', subPackage.replaceAll(/\./g, '-'))
          .replaceAll('{package}', subPackage)
          .replaceAll('{subPackage}', subPackage)
          .replaceAll('{mainClass}', mainClass)
          .replaceAll('{clientClass}', clientClass)
          .replaceAll('{datagenClass}', datagenClass)
          .replaceAll('{postfix}', '')
    }


    //build the fabric json
    let fabric = JSON.parse(loadAndReplace(Path.join(process.cwd(), 'include', 'mod.json')))

    const mainMixin = JSON.parse(loadAndReplace(Path.join(process.cwd(), 'include', 'mixin.json')))
    mainMixin.mixins = []

    const clientMixin = JSON.parse(loadAndReplace(Path.join(process.cwd(), 'include', 'mixin.json')))
    clientMixin.client = []
    clientMixin.package = `org.betterx.wover.${options.namespace}.mixin.client`

    const accesswidener = loadAndReplace(Path.join(process.cwd(), 'include', 'mod.accesswidener'))

    const mainJava = loadAndReplace(Path.join(process.cwd(), 'include', 'Main.java'))
    const clientJava = loadAndReplace(Path.join(process.cwd(), 'include', 'Client.java'))
    const datagenJava = loadAndReplace(Path.join(process.cwd(), 'include', 'Datagen.java'))


    //build the gradle build file
    const build = loadAndReplace(Path.join(process.cwd(), 'include', 'gradle.build'))
    const basePath = Path.resolve(Path.join(process.cwd(), '..', `wover-${options.namespace}-api`))


    console.log(`Creating SubModule ${options.title}...`)
    console.log(`  - Building folder structure in '${basePath}'`)
    mkdir(basePath);

    const builderPath = Path.join(basePath, 'build.gradle');
    if (!fs.existsSync(builderPath)){
        console.log(`    - Adding build.gradle`)
        fs.writeFileSync(builderPath, build)
    }


    const srcPath = mksub(basePath, 'src')
    console.log(`  - Building main folder structure in '${basePath}'`)
    const mainPath = mksub(srcPath, 'main')
    const javaPath = mksub(mainPath, 'java')
    const entryPath = mksub(javaPath, Path.join('org','betterx','wover','entrypoint'))
    const resourcesPath = mksub(mainPath, 'resources')

    if (fs.existsSync(Path.join(resourcesPath, 'fabric.mod.json'))){
        let old = fabric;
        fabric = JSON.parse(loadAndReplaceTest(Path.join(resourcesPath, 'fabric.mod.json')))
        fabric.mixins = old.mixins
    }

    if (options.main){
        const packagePath = mksub(javaPath, Path.join('org','betterx','wover', options.namespace))
        const apiPath = mksub(packagePath, 'api')
        const implPath = mksub(packagePath, 'impl')
        const mixinPath = mksub(packagePath, 'mixin')
        const assetsPath = mksub(resourcesPath, `assets/wover-${options.namespace}`)

        console.log(`    - Adding mixin config`)
        fs.writeFileSync(Path.join(resourcesPath, `wover.${options.namespace}.mixins.common.json`), JSON.stringify(mainMixin, null, 2))

        console.log(`    - Adding accesswidener config`)
        fs.writeFileSync(Path.join(resourcesPath, `wover-${options.namespace.replaceAll(/\./g, '-')}.accesswidener`), accesswidener)
        fabric.accessWidener = `wover-${options.namespace.replaceAll(/\./g, '-')}.accesswidener`

        console.log(`    - Adding Entrypoint`)
        fs.writeFileSync(Path.join(entryPath, `${mainClass}.java`), mainJava)
    }

    if (options.client){
        console.log(`  - Building client folder structure in '${basePath}'`)
        const clientPath = mksub(srcPath, 'client')
        const clientJavaPath = mksub(clientPath, 'java')
        const clientEntryPath = mksub(clientJavaPath, 'org/betterx/wover/entrypoint/client')
        const clientPackagePath = mksub(clientJavaPath, Path.join('org','betterx','wover', options.namespace))
        const clientApiPath = mksub(clientPackagePath, Path.join('api', 'client'))
        const clientImplPath = mksub(clientPackagePath, Path.join('impl', 'client'))
        const clientMixinPath = mksub(clientPackagePath, Path.join('mixin', 'client'))

        fabric.entrypoints.client = [`org.betterx.wover.entrypoint.client.${clientClass}`]
        fabric.mixins.push(`wover.${options.namespace}.mixins.client.json`)

        console.log(`    - Adding client mixin config`)
        fs.writeFileSync(Path.join(resourcesPath, `wover.${options.namespace}.mixins.client.json`), JSON.stringify(clientMixin, null, 2))

        console.log(`    - Adding client Entrypoint`)
        fs.writeFileSync(Path.join(clientEntryPath, `${clientClass}.java`), clientJava)
    }

    if (options.datagen){
        console.log(`  - Building datagen folder structure in '${basePath}'`)
        const datagenPath = mksub(srcPath, 'datagen')
        const javaPath = mksub(datagenPath, 'java')
        const entryPath = mksub(javaPath, `org/betterx/wover/${options.namespace}/datagen`)

        fabric.entrypoints['fabric-datagen'] = [`org.betterx.wover.${options.namespace}.datagen.${datagenClass}`]

        console.log(`    - Adding datagen Entrypoint`)
        fs.writeFileSync(Path.join(entryPath, `${datagenClass}.java`), datagenJava)
    }

    const anyTest = options.test || options.clientTest || options.datagenTest
    if (anyTest){
        const mainTestJava = loadAndReplaceTest(Path.join(process.cwd(), 'include', 'TestMod.java'))
        const clientTestJava = loadAndReplaceTest(Path.join(process.cwd(), 'include', 'Client.java'))

        let fabric = JSON.parse(loadAndReplaceTest(Path.join(process.cwd(), 'include', 'mod.json')))
        fabric.mixins = []

        console.log(`  - Building testmod folder structure in '${basePath}'`)
        const testPath = mksub(srcPath, 'testmod')
        const javaPath = mksub(testPath, 'java')
        const entryPath = mksub(javaPath, Path.join('org','betterx','wover','testmod', 'entrypoint',))
        const resourcesPath = mksub(testPath, 'resources')
        if (fs.existsSync(Path.join(resourcesPath, 'fabric.mod.json'))){
            fabric = JSON.parse(loadAndReplaceTest(Path.join(resourcesPath, 'fabric.mod.json')))
        }

        if (options.datagenTest){
            const datagenTestJava = loadAndReplaceTest(Path.join(process.cwd(), 'include', 'Datagen.java'))
            console.log(`  - Building datagen testmod folder structure in '${basePath}'`)
            const datagenPath = mksub(srcPath, 'testmodDatagen')
            const javaPath = mksub(datagenPath, 'java')
            const entryPath = mksub(javaPath, `org/betterx/wover/testmod/${options.namespace}/datagen`)

            fabric.entrypoints['fabric-datagen'] = [`org.betterx.wover.testmod.${options.namespace}.datagen.${datagenTestClass}`]

            console.log(`    - Adding datagen Entrypoint`)
            fs.writeFileSync(Path.join(entryPath, `${datagenTestClass}.java`), datagenTestJava)
        }

        if (options.main || options.test){
            const packagePath = mksub(javaPath, Path.join('org','betterx','wover', 'testmod', options.namespace))
            const assetsPath = mksub(resourcesPath, `assets/wover-${options.namespace}-testmod`)

            fabric.entrypoints.main = [`org.betterx.wover.testmod.entrypoint.${mainTestClass}`]

            console.log(`    - Adding testmod Entrypoint`)
            fs.writeFileSync(Path.join(entryPath, `${mainTestClass}.java`), mainTestJava)
        }

        if (options.client || options.clientTest){
            console.log(`  - Building client testmod folder structure in '${basePath}'`)
            const clientTestPath = mksub(srcPath, 'testmodClient')
            const javaPath = mksub(clientTestPath, 'java')
            const entryPath = mksub(javaPath, Path.join('org','betterx','wover','testmod', 'entrypoint','client'))
            const packagePath = mksub(javaPath, Path.join('org','betterx','wover', 'testmod', options.namespace, 'client'))

            console.log(`    - Adding client testmod Entrypoint`)
            fs.writeFileSync(Path.join(entryPath, `${clientTestClass}.java`), clientTestJava)

            fabric.entrypoints.client = [`org.betterx.wover.testmod.entrypoint.client.${clientTestClass}`]
        }

        console.log(`  - Adding fabric.mod.json for testmod`)
        fs.writeFileSync(Path.join(resourcesPath, 'fabric.mod.json'), JSON.stringify(fabric, null, 2))
    }

    console.log(`  - Adding fabric.mod.json`)
    fs.writeFileSync(Path.join(resourcesPath, 'fabric.mod.json'), JSON.stringify(fabric, null, 2))
}