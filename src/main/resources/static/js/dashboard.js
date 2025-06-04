document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const raceSelect = document.getElementById('raceSelect');
    const driverSelect = document.getElementById('driverSelect');
    const refreshBtn = document.getElementById('refreshBtn');
    const statsDisplay = document.getElementById('statsDisplay');
    const trackMap = document.getElementById('trackMap');
    const currentLap = document.getElementById('currentLap');
    const timelineSlider = document.getElementById('timelineSlider');
    const lapDataTable = document.getElementById('lapDataTable').querySelector('tbody');
    const telemetryChart = document.getElementById('telemetryChart');
    
    // Sample data (will be replaced with API calls)
    let drivers = [];
    let selectedDriverData = null;
    let lapData = [];
    
    // Event listeners
    raceSelect.addEventListener('change', handleRaceChange);
    driverSelect.addEventListener('change', handleDriverChange);
    refreshBtn.addEventListener('click', refreshData);
    timelineSlider.addEventListener('input', updateTimelineView);
    
    // Functions
    function handleRaceChange() {
        const selectedRace = raceSelect.value;
        if (!selectedRace) {
            resetDriverSelect();
            return;
        }
        
        console.log("Selected race:", selectedRace);
        
        // Fetch drivers for selected race
        fetchDriversForRace(selectedRace);
    }
    
    function handleDriverChange() {
        const selectedDriver = driverSelect.value;
        if (!selectedDriver) {
            resetDriverDisplay();
            return;
        }
        
        // Fetch driver data
        fetchDriverData(selectedDriver);
    }
    
    function fetchDriversForRace(raceId) {
        // Replace with actual API call
        fetch(`/api/races/${raceId}/drivers`)
            .then(response => response.json())
            .then(data => {
                drivers = data;
                populateDriverSelect(drivers);
                driverSelect.disabled = false;
            })
            .catch(error => {
                console.error('Error fetching drivers:', error);
                // For demo, use sample data
                drivers = getSampleDrivers();
                populateDriverSelect(drivers);
                driverSelect.disabled = false;
            });
    }
    
    function fetchDriverData(driverId) {
        // Replace with actual API call
        fetch(`/api/drivers/${driverId}`)
            .then(response => response.json())
            .then(data => {
                selectedDriverData = data;
                displayDriverStats(selectedDriverData);
                
                // Now fetch lap data
                return fetch(`/api/drivers/${driverId}/laps`);
            })
            .then(response => response.json())
            .then(data => {
                lapData = data;
                initializeTimeline(lapData);
                displayLapData(lapData);
                updateTrackMap(lapData[0]);
                initializeTelemetryChart(lapData);
            })
            .catch(error => {
                console.error('Error fetching driver data:', error);
                // For demo, use sample data
                selectedDriverData = getSampleDriverData(driverId);
                displayDriverStats(selectedDriverData);
                
                lapData = getSampleLapData(driverId);
                initializeTimeline(lapData);
                displayLapData(lapData);
                updateTrackMap(lapData[0]);
                initializeTelemetryChart(lapData);
            });
    }
    
    function populateDriverSelect(drivers) {
        driverSelect.innerHTML = '<option value="">-- Select a driver --</option>';
        drivers.forEach(driver => {
            const option = document.createElement('option');
            option.value = driver.driverId;
            option.textContent = driver.fullName;
            driverSelect.appendChild(option);
        });
    }
    
    function displayDriverStats(driver) {
        statsDisplay.innerHTML = `
            <div class="driver-name">${driver.fullName}</div>
            <div class="team-name">${driver.team}</div>
            <div class="stats-item">
                <span class="stats-label">Car Number:</span> ${driver.carNumber}
            </div>
            <div class="stats-item">
                <span class="stats-label">Position:</span> ${driver.position}
            </div>
            <div class="stats-item">
                <span class="stats-label">Points:</span> ${driver.points}
            </div>
            <div class="stats-item">
                <span class="stats-label">Fastest Lap:</span> ${driver.fastestLap}
            </div>
            <div class="stats-item">
                <span class="stats-label">Avg Speed:</span> ${driver.avgSpeed} km/h
            </div>
        `;
    }
    
    function initializeTimeline(lapData) {
        const totalLaps = lapData.length;
        timelineSlider.min = 1;
        timelineSlider.max = totalLaps;
        timelineSlider.value = 1;
        currentLap.textContent = '1';
        timelineSlider.disabled = false;
    }
    
    function updateTimelineView() {
        const lapIndex = parseInt(timelineSlider.value) - 1;
        currentLap.textContent = timelineSlider.value;
        
        // Update track map for current lap
        updateTrackMap(lapData[lapIndex]);
        
        // Highlight the current lap in table
        const rows = lapDataTable.querySelectorAll('tr');
        rows.forEach(row => row.classList.remove('lap-highlight'));
        if (rows[lapIndex]) {
            rows[lapIndex].classList.add('lap-highlight');
            rows[lapIndex].scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        
        // Update telemetry chart
        updateTelemetryChart(lapIndex);
    }
    
    function displayLapData(lapData) {
        lapDataTable.innerHTML = '';
        
        lapData.forEach(lap => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${lap.lapNumber}</td>
                <td>${lap.lapTime}</td>
                <td>${lap.speed}</td>
                <td>${lap.compound}</td>
                <td>${lap.position}</td>
            `;
            lapDataTable.appendChild(row);
        });
    }
    
    function updateTrackMap(lapData) {
        // Create a more visual representation of the track with driver position
        const trackSvg = `
            <svg width="100%" height="100%" viewBox="0 0 400 200" xmlns="http://www.w3.org/2000/svg">
                <!-- Track outline -->
                <path d="M50,100 C50,50 150,30 200,30 C250,30 350,50 350,100 C350,150 250,170 200,170 C150,170 50,150 50,100 Z" 
                      fill="none" stroke="#333" stroke-width="20" />
                
                <!-- Track center line -->
                <path d="M50,100 C50,50 150,30 200,30 C250,30 350,50 350,100 C350,150 250,170 200,170 C150,170 50,150 50,100 Z" 
                      fill="none" stroke="white" stroke-width="2" stroke-dasharray="5,5" />
                
                <!-- Start/Finish line -->
                <line x1="40" y1="100" x2="60" y2="100" stroke="white" stroke-width="4" />
                
                <!-- Driver car representation -->
                <circle id="driverCar" cx="50" cy="100" r="8" fill="red" stroke="white" stroke-width="1" />
                
                <!-- Sector markers -->
                <circle cx="200" cy="30" r="5" fill="yellow" />
                <circle cx="350" cy="100" r="5" fill="yellow" />
                <circle cx="200" cy="170" r="5" fill="yellow" />
            </svg>
        `;
        
        // Calculate driver position on track based on lap number and position
        // This is a simplified visualization
        const lapProgress = (lapData.lapNumber % 10) / 10;
        
        trackMap.innerHTML = `
            <div class="position-relative" style="height: 200px;">
                ${trackSvg}
                <div class="position-absolute top-0 end-0 bg-light p-2 m-2 rounded shadow-sm">
                    <div class="fw-bold">Lap ${lapData.lapNumber}</div>
                    <div>Position: ${lapData.position}</div>
                    <div>Tires: ${lapData.compound}</div>
                    <div>Speed: ${lapData.speed} km/h</div>
                </div>
            </div>
        `;
        
        // Animate driver position
        setTimeout(() => {
            const driverCar = document.getElementById('driverCar');
            if (driverCar) {
                try {
                    // Calculate position on elliptical track based on lap progress
                    // Fallback for browsers that don't support motion path
                    const pathPoints = [
                        {x: 50, y: 100},   // Start/finish
                        {x: 100, y: 50},   // Turn 1
                        {x: 200, y: 30},   // Sector 1
                        {x: 300, y: 50},   // Turn 2
                        {x: 350, y: 100},  // Sector 2
                        {x: 300, y: 150},  // Turn 3
                        {x: 200, y: 170},  // Sector 3
                        {x: 100, y: 150}    // Turn 4 (back to start)
                    ];
                    
                    // Find position along the path based on progress
                    const pointIndex = Math.floor(lapProgress * (pathPoints.length - 1));
                    const nextPointIndex = (pointIndex + 1) % pathPoints.length;
                    const pointProgress = (lapProgress * (pathPoints.length - 1)) % 1;
                    
                    const currentPoint = pathPoints[pointIndex];
                    const nextPoint = pathPoints[nextPointIndex];
                    
                    // Linear interpolation between points
                    const x = currentPoint.x + (nextPoint.x - currentPoint.x) * pointProgress;
                    const y = currentPoint.y + (nextPoint.y - currentPoint.y) * pointProgress;
                    
                    // Try to use modern motion path if supported
                    if (CSS.supports('offset-distance', '0%') || CSS.supports('motion-offset', '0%')) {
                        driverCar.style.offsetPath = "path('M50,100 C50,50 150,30 200,30 C250,30 350,50 350,100 C350,150 250,170 200,170 C150,170 50,150 50,100 Z')";
                        driverCar.style.offsetDistance = `${lapProgress * 100}%`;
                    } else {
                        // Fallback to direct positioning
                        driverCar.setAttribute('cx', x);
                        driverCar.setAttribute('cy', y);
                    }
                } catch (e) {
                    console.error("Error updating driver position:", e);
                    // Direct fallback
                    const angle = lapProgress * 2 * Math.PI;
                    const radius = 70;
                    const centerX = 200;
                    const centerY = 100;
                    const x = centerX + radius * Math.cos(angle);
                    const y = centerY + radius * Math.sin(angle);
                    
                    driverCar.setAttribute('cx', x);
                    driverCar.setAttribute('cy', y);
                }
            }
        }, 100);
    }
    
    function initializeTelemetryChart(lapData) {
        // Create a placeholder for telemetry data
        telemetryChart.innerHTML = `
            <canvas id="speedChart"></canvas>
        `;
        
        try {
            const ctx = document.getElementById('speedChart').getContext('2d');
            
            // Extract data for chart
            const labels = lapData.map(lap => "Lap " + lap.lapNumber);
            const speeds = lapData.map(lap => lap.speed);
            
            console.log("Chart data:", labels, speeds);
            
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Speed (km/h)',
                        data: speeds,
                        backgroundColor: 'rgba(255, 0, 0, 0.2)',
                        borderColor: 'rgba(255, 0, 0, 1)',
                        borderWidth: 2,
                        tension: 0.3,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Speed per Lap'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: false,
                            title: {
                                display: true,
                                text: 'Speed (km/h)'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Lap Number'
                            }
                        }
                    }
                }
            });
        } catch (error) {
            console.error("Error initializing chart:", error);
            telemetryChart.innerHTML = `
                <div class="alert alert-danger">
                    <p>Error initializing telemetry chart.</p>
                    <p>Error details: ${error.message}</p>
                </div>
            `;
        }
    }
    
    function updateTelemetryChart(lapIndex) {
        // In a real app, this would update the telemetry chart
        // based on the selected lap
    }
    
    function refreshData() {
        if (raceSelect.value && driverSelect.value) {
            fetchDriverData(driverSelect.value);
        }
    }
    
    function resetDriverSelect() {
        driverSelect.innerHTML = '<option value="">-- Select a driver --</option>';
        driverSelect.disabled = true;
        resetDriverDisplay();
    }
    
    function resetDriverDisplay() {
        statsDisplay.innerHTML = '<p class="text-center">Select a race and driver to view statistics</p>';
        trackMap.innerHTML = `
            <div class="text-center p-5">
                <h5>Track Map Visualization</h5>
                <p>Select a race and driver to view track position</p>
            </div>
        `;
        timelineSlider.disabled = true;
        timelineSlider.value = 0;
        currentLap.textContent = '0';
        lapDataTable.innerHTML = '<tr><td colspan="5" class="text-center">No data available</td></tr>';
        telemetryChart.innerHTML = `
            <div class="text-center p-5">
                <h5>Telemetry Visualization</h5>
                <p>Select a race and driver to view telemetry data</p>
            </div>
        `;
    }
    
    // Sample data functions for demo purpose
    function getSampleDrivers() {
        return [
            { driverId: 'VER', fullName: 'Max Verstappen' },
            { driverId: 'HAM', fullName: 'Lewis Hamilton' },
            { driverId: 'LEC', fullName: 'Charles Leclerc' },
            { driverId: 'NOR', fullName: 'Lando Norris' },
            { driverId: 'SAI', fullName: 'Carlos Sainz' }
        ];
    }
    
    function getSampleDriverData(driverId) {
        const driverMap = {
            'VER': {
                driverId: 'VER',
                fullName: 'Max Verstappen',
                team: 'Red Bull Racing',
                carNumber: 1,
                position: 1,
                points: 350,
                fastestLap: '1:34.567',
                avgSpeed: 212.5
            },
            'HAM': {
                driverId: 'HAM',
                fullName: 'Lewis Hamilton',
                team: 'Mercedes',
                carNumber: 44,
                position: 2,
                points: 310,
                fastestLap: '1:34.890',
                avgSpeed: 211.2
            },
            'LEC': {
                driverId: 'LEC',
                fullName: 'Charles Leclerc',
                team: 'Ferrari',
                carNumber: 16,
                position: 3,
                points: 290,
                fastestLap: '1:35.123',
                avgSpeed: 210.8
            },
            'NOR': {
                driverId: 'NOR',
                fullName: 'Lando Norris',
                team: 'McLaren',
                carNumber: 4,
                position: 4,
                points: 244,
                fastestLap: '1:35.890',
                avgSpeed: 209.7
            },
            'SAI': {
                driverId: 'SAI',
                fullName: 'Carlos Sainz',
                team: 'Ferrari',
                carNumber: 55,
                position: 5,
                points: 236,
                fastestLap: '1:36.012',
                avgSpeed: 208.9
            }
        };
        
        return driverMap[driverId] || driverMap['VER'];
    }
    
    function getSampleLapData(driverId) {
        const tireCompounds = ['Soft', 'Medium', 'Hard'];
        const lapData = [];
        
        // Generate 10 sample laps
        for (let lap = 1; lap <= 10; lap++) {
            const randomSpeed = 205 + Math.floor(Math.random() * 30);
            const randomPosition = 1 + Math.floor(Math.random() * 5);
            const randomCompound = tireCompounds[Math.floor(Math.random() * tireCompounds.length)];
            const baseTime = 95000; // 1:35.000
            const randomVariation = Math.floor(Math.random() * 5000) - 2500; // +/- 2.5 seconds
            const milliseconds = baseTime + randomVariation;
            
            // Format time as mm:ss.sss
            const minutes = Math.floor(milliseconds / 60000);
            const seconds = ((milliseconds % 60000) / 1000).toFixed(3);
            const formattedTime = `${minutes}:${seconds.padStart(6, '0')}`;
            
            lapData.push({
                lapNumber: lap,
                lapTime: formattedTime,
                speed: randomSpeed,
                position: randomPosition,
                compound: randomCompound
            });
        }
        
        return lapData;
    }
});