
# Disable threads support in bind
#
# It looks like when dhcp/dhclient is running in nowait mode (-nw option). it
# relays on bind libraries (libisc.so) to work with thread support disabled
#
# With thread support enabled in bind, dhclient is waiting in 
# libisc.so/isc.app_ctxrun() in a pthread_cond_wait() while there is no other 
# thread running, making dhclient non-functional
# With thread support disabled, libisc is running correctly with dhclient
#
# The previous bind version 9.10.2-P4-r0 (as supported in meta-bluebox), had 
# the thread support disabled by default in the bind recipe.


EXTRA_OECONF_remove = "--enable-threads"
EXTRA_OECONF_append += " --disable-threads "
