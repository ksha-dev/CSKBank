document.addEventListener("DOMContentLoaded", function() {
    var searchInput = document.getElementById('searchInput');
    var table = document.getElementById('accountsTable');
    var rows = table.getElementsByTagName('tr');

    searchInput.addEventListener('keyup', function() {
        var searchTerm = this.value.toLowerCase();
        Array.from(rows).forEach(function(row) {
            var accountNumber = row.getElementsByTagName('td')[0];
            if (accountNumber) {
                var textValue = accountNumber.textContent || accountNumber.innerText;
                if (textValue.toLowerCase().indexOf(searchTerm) > -1) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    });
});