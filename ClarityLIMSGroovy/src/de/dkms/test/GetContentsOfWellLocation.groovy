// Cookbook Example Script for Clarity LIMS
import test_api.GLSRestApiUtils
import test_api.ProxySetup

/**
 * Description:
 *
 * This example shows how to find the contents of a specific well placement.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -c CONTAINERLIMSID")
cli.h(argName:'hostname',        longOpt:'hostname',        required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',        longOpt:'username',        required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',        longOpt:'password',        required:true, args:1, 'LIMS password (Required)')
cli.c(argName:'containerLIMSID', longOpt:'containerLIMSID', required:true, args:1, 'LIMS ID of the container (Required)')
cli.pl(argName:'placement',      longOpt:'placement',       required:true, args:1, valueSeparator: ',', 'Placement searched (Required)')

def opt = cli.parse(args)
if (!opt) {
	System.exit(-1)
}

hostname = opt.h
username = opt.u
password = opt.p
containerLIMSID = opt.c
targetPlacement = opt.pl
println targetPlacement
ProxySetup.setProxy();
// Determine the specified container's URI and retrieve it
containerURI = "https://${hostname}/api/v2/containers/${containerLIMSID}"
container = GLSRestApiUtils.httpGET(containerURI, username, password)

// Print the artifact that is located at the specified placement
List <String> contents = container.placement.findAll { it.value.text() == targetPlacement }
println contents.size()
//for content in contents {
//	artifactURI = contents?.@uri
//	println artifactURI
//}