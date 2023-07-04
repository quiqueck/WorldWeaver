import commandLineArgs from 'command-line-args'
import commandLineUsage from 'command-line-usage'
import {createDefinitions, createSections, validateCreateOptions, create} from './lib/projectBuilder.js'

/* first - parse the main command */
const mainDefinitions = [
  { name: 'command', multiple:false, defaultOption: true }
]
const mainOptions = commandLineArgs(mainDefinitions, { stopAtFirstUnknown: true })
const argv = mainOptions._unknown || []
const mainHeader = [{
        header: 'Wover CLI',
        content: 'Utility Libary for {italic WorldWeaver}. '
}]

if (mainOptions.command === 'create') {
    const options = commandLineArgs(createDefinitions, { argv })

    if (!validateCreateOptions(options)) {
        const createUsage = commandLineUsage([...mainHeader, ...createSections])
        console.log(createUsage)
        process.exit(-1)
    }

    create(options);
} else {
    let helpUsage = undefined

    if (mainOptions.command === 'help') {
        const definitions = [
            { name: 'subcommand', type: String, defaultOption: true },
        ]
        const options = commandLineArgs(definitions, { argv })
        if (options.subcommand === 'create') helpUsage = createUsage
    }

    if (helpUsage===undefined) {
        const mainSections = [
          ...mainHeader,
          {
            header: 'Synopsis',
            content: '$ node wover.js <command> <options> '
          },
          {
            header: 'Command List',
            content: [
              { name: 'help', summary: 'Display help information.' },
              { name: 'create', summary: 'Creates a new Wover Submodule' }
            ]
          }
        ]
        helpUsage = commandLineUsage(mainSections)
    }
    console.log(helpUsage)

}