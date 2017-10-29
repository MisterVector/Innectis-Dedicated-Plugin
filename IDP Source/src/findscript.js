
// looks for the command in the table
function find(command) {
    
    // Looks for row
    rows = $('.ct tbody').find('.r');    
    command = command.toLowerCase();
    
    // checks length
    if (command.length < 2) {
        $('.hidden').removeClass('hidden');
        // Clear styles
        rows.find('.c').attr('style', '');
        rows.find('.a').attr('style', '');
        return 0;
    }
    
    // Keep track of command count
    var counter = 0;
    var first = true;
    
    // For each row
    rows.each(function(indx) {
        var found = false;
        // Get JQUERY row
        row = $(rows[indx]);
        
        // Find main
        cmd = row.find('.c');
        cmd.attr('style', '');
        
        if (cmd.html().toLowerCase().indexOf(command) > -1) {
            cmd.attr('style', 'background-color: orange;');
            found = true;
        //if (first) row.find('a').focus();
        }
        
        // Find the alternatives
        cmd = row.find('.a');
        cmd.attr('style', '');
            
        if (cmd.html().toLowerCase().indexOf(command) > -1) {
            cmd.attr('style', 'background-color: orange;');
            found = true;
        //if (first) row.find('a').focus();
        }
        
        // If found add to counter
        if (found) {
            counter++;
            first = false;
            row.removeClass('hidden');
        } else {
            row.addClass('hidden');
        }
    });
    
    // Return command count
    return counter;
};

$(document).ready(function () { 
    $("#i").keyup(function() {
        var input = $(this);        
        var val = input.val();        
        
        // focus on bottom link
        $(".zzl").focus();
        
        // Look for result(will focus auto on first result);
        var results = find(val);
        
        // print results
        var parent = input.closest("div");
        parent.find(".r").html( results + " results found!");
        
        // Focus back to input
        input.focus();        
    });
});