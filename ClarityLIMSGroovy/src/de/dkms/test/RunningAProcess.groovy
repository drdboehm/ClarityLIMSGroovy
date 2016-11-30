// Cookbook Example Script for Clarity LIMS
import test_api.GLSRestApiUtils
import groovy.xml.StreamingMarkupBuilder
import test_api.ProxySetup

/**
 * Description:
 *
 * This example shows how to run a process via script.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -pt PROCESSTYPEURI -a ANALYTELIMSID1 ANALYTELIMSID2 ANALYTELIMSID3")
cli.h(argName:'hostname',        longOpt:'hostname',       required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',        longOpt:'username',       required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',        longOpt:'password',       required:true, args:1, 'LIMS password (Required)')
cli.pt(argName:'processTypeUri', longOpt:'processTypeUri', required:true, args:1, 'The URI of the process type to create (Required)')
cli.a(argName:'analyteLIMSIDs',  longOpt:'anaylteLIMSIDs', required:true, args:3, 'LIMS IDs of 3 analytes (Required)')

def opt = cli.parse(args)
if (!opt) {
	System.exit(-1)
}
ProxySetup.setProxy()
hostname = opt.h
username = opt.u
password = opt.p
processTypeURI = opt.pt
analyteLIMSIDs = opt.as


// Determine the containers list URI
String containersListURI = "https://${hostname}/api/v2/containers"

def builder = new StreamingMarkupBuilder()
builder.encoding = "UTF-8"
Random randomNumGen = new Random(System.currentTimeMillis() + Runtime.runtime.freeMemory())

// Create a new container using the Markup Builder
def containerDoc = builder.bind {
	mkp.xmlDeclaration()
	mkp.declareNamespace(con: 'http://genologics.com/ri/container')
	mkp.declareNamespace(udf: 'http://genologics.com/ri/userdefined')
	'con:container'{
		'name'("HiSEQ POST${randomNumGen.nextInt()}")
		'type'(uri:"http://${hostname}/api/v2/containertypes/1", name:"96 well plate")
	}
}
// Post the new container to the API
containerNode = GLSRestApiUtils.xmlStringToNode(containerDoc.toString())
returnNode = GLSRestApiUtils.httpPOST(containerNode, containersListURI, username, password)
container96WellsURI = returnNode.@uri

// Determine the list URIs and the specified analyte URIs
processListURI = "https://${hostname}/api/v2/processes"
researcherURI = "https://${hostname}/api/v2/researchers/1"
analyte1URI = "https://${hostname}/api/v2/artifacts/${analyteLIMSIDs[0]}"
analyte2URI = "https://${hostname}/api/v2/artifacts/${analyteLIMSIDs[1]}"
analyte3URI = "https://${hostname}/api/v2/artifacts/${analyteLIMSIDs[2]}"
// Retrieve the process type
processTypeNode = GLSRestApiUtils.httpGET(processTypeURI, username, password)

// Create a new process using the Markup Builder
def processDoc = new StreamingMarkupBuilder().bind {
	mkp.xmlDeclaration()
	mkp.declareNamespace(prx: 'http://genologics.com/ri/processexecution')
	'prx:process'{
		'type'(processTypeNode.'@name')
		'technician'(uri:researcherURI)
		'input-output-map' {
			'input'(uri:analyte1URI)
			'output'(type:'Analyte') {
				'location' {
					'container'(uri:container96WellsURI)
					'value'("A:1")
				}
			}
		}
		'input-output-map' {
			'input'(uri:analyte2URI)
			'output'(type:'Analyte') {
				'location' {
					'container'(uri:container96WellsURI)
					'value'("A:2")
				}
			}
		}
		'input-output-map' {
			'input'(uri:analyte3URI)
			'output'(type:'Analyte') {
				'location' {
					'container'(uri:container96WellsURI)
					'value'("A:3")
				}
			}
		}
		'input-output-map'(shared:'true') {
			'input'(uri:analyte1URI)
			'input'(uri:analyte2URI)
			'input'(uri:analyte3URI)
			'output'(type:'ResultFile')
		}
	}
}
// Post the new process to the API
unresolvedProcessNode = GLSRestApiUtils.xmlStringToNode(processDoc.toString())
returnNode = GLSRestApiUtils.httpPOST(unresolvedProcessNode, "${processListURI}", username, password)
println GLSRestApiUtils.nodeToXmlString(returnNode)
