function CCH() {
    
    this.showToast = function(stri) {
        Android.showToast(stri);
    },

    this.getUsername = function() {
    	return Android.getUsername();
    },

    // Facilities
    this.getNumFacilities = function() {
    	return Android.getNumFacilities();
    },
    
    this.getFacilityName = function(fid) {
    	return Android.getFacilityName(fid);
    },

    this.getFacilityList = function() {
        return Android.getFacilityList();
    },

    this.getFacilityEventsList = function(period, id) {
        return Android.getFacilityEventsList(period, id);
    },

    this.getFacilityNurses = function(id) {
        return Android.getFacilityNurses(id);
    },

    // Nurses
    this.getNumNurses = function() {
    	return Android.getNumNurses();
    },
    
    this.getNurseName = function(nid) {
    	return Android.getNurseName(nid);
    },

    this.getNurseList = function() {
        return Android.getNurseList();
    },

    this.getNurseEventsList = function(period, id) {
        return Android.getNurseEventsList(period, id);
    },

    this.getNurseTargets = function(nid) {
    	return Android.getNurseTargets(nid);
    },

    this.getNurseCourses = function(nid) {
    	return Android.getNurseCourses(nid);
    },


   
    // Events
    this.refreshEvents = function() {
        Android.refreshEvents();
    },
    
    this.numEventsCompleted = function() {
    	return Android.numEventsCompleted();
    },

    this.getNumEventsToday = function() {
    	return Android.getNumEventsToday();
    },
    
    this.getEventsList = function(period) {
    	return Android.getEventsList(period);
    },

    this.getTodaysEventsSnippet = function() {
    	return Android.getTodaysEventsSnippet();
    },
    
    this.getPreviousLocations = function() {
        var s = Android.getPreviousLocations();
        var data = JSON.parse(s);
        return data.myLocations;
    }
}

$(document).ready(function()  {

    var cch = new CCH();

    if ($('#username')) {
    	$('#username').html(cch.getUsername());
    }

    if ($('#planner')) {
    	num = cch.getNumEventsToday();
        num = (num=="0") ? "" : num;
    	$('#plannerbadge').html(num);
    }
    if ($('#cchfacility')) {
    	num = cch.getNumFacilities();
        num = (num=="0") ? "" : num;
    	$('#facilitybadge').html(num);
    }
    if ($('#cchnurse')) {
    	num = cch.getNumNurses();
        num = (num=="0") ? "" : num;
    	$('#nursebadge').html(num);
    }
    
    $('.gotoevent').click(function(e) {
         var url = $(this).data('url');
         window.location = url; 
    });
    
    $('.featurette').click(function(e) {
         var url = $(this).data('url');
         window.location = url;
    });
});


