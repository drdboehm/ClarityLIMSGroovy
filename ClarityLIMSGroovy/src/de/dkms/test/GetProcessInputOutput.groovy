// Cookbook Example Script for Clarity LIMS
import test_api.GLSRestApiUtils
import test_api.ProxySetup

/**
 * Description:
 *
 * This example shows how to iterate through the input-output map of a process.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -pr PROCESSLIMSID")
cli.h(argName:'hostname',       longOpt:'hostname',      required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',       longOpt:'username',      required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',       longOpt:'password',      required:true, args:1, 'LIMS password (Required)')
cli.pr(argName:'processLIMSID', longOpt:'processLIMSID', required:true, args:1, 'LIMS ID of a process (Required)')

def opt = cli.parse(args)
if (!opt) {
	System.exit(-1)
}

hostname = opt.h
username = opt.u
password = opt.p
processLIMSID = opt.pr
ProxySetup.setProxy()
outputToInputMap = [:]
processURI = "https://${hostname}/api/v2/processes/${processLIMSID}"
process = GLSRestApiUtils.httpGET(processURI, username, password)

// For each io-map in the process, add its information to outputToInputMap
process.'input-output-map'.each {
	outputType = it.'output'[0].'@output-type'
	outputLIMSID = it.'output'[0].@limsid
	inputLIMSID = it.'input'[0].@limsid

	// outputToInputMap stores all the output type and LIMSIDs of all the inputs to the output
	if ((!outputToInputMap[outputLIMSID])) {
		if (outputType == ('Analyte')) {
			outputToInputMap[outputLIMSID] = [outputType, inputLIMSID]
		}
	} else {
		// If entry already exists, add another input to the list
		outputToInputMap[outputLIMSID] << inputLIMSID
	}
}

// Print the contents of the map, which stores the inputs LIMSIDs under the output's LIMSID
outputToInputMap.each { key, value ->
	print "$key is a(n) ${value[0]} with input:   "
	for (int i = 1; i < value.size(); i++) {
		println '\t' + value[i]
	}
}
