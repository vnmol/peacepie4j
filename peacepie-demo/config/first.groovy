println "Groovy bootstrap started"


println "PekkoAdmin: ${pekkoAdmin}"
println "PeacepieMessage: ${PeacepieMessage}"

msg = PeacepieMessage.getMsg("01", "Hello!", null, null, null, 0, null);

pekkoAdmin.nodeAdminActor.tell(msg)

println "Groovy bootstrap finished"