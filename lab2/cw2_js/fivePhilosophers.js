// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy. 
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp 
//    do widelców. Wyniki przedstaw na wykresach.

var Fork = function () {
    this.state = 0;
    this.maxNumberOfIteration = 10;
    return this;
}

Fork.prototype.acquire = function (cb) {
    var getTime = iteration => {
        biggestThreshold = Math.pow(2, iteration);
        var randomTime = Math.floor(Math.random() * biggestThreshold);
        return randomTime;
    };

    var handler = i => {
        if (this.state == 0) {
            this.state = 1;
            return cb();
        }
        setTimeout(handler, getTime(i), Math.min(i + 1, this.maxNumberOfIteration));
    }
    setTimeout(handler, 1, 1);
}

Fork.prototype.release = function () {
    this.state = 0;
}

var Philosopher = function (id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id + 1) % forks.length;
    return this;
}

Philosopher.prototype.startNaive = function (count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        thinkTime = 500,
        eatingTime = 500,

        loopNaive = function (numberOfMeals) {
            var startTime = new Date().getTime();
            if (numberOfMeals > 0)

                forks[f1].acquire(function () {
                    console.log("Philosopher " + id + " took the left fork");
                    forks[f2].acquire(function () {
                        var stopTime = new Date().getTime();
                        console.log("Philosopher: #" + id + "# time: #" + stopTime - startTime + "#");
                        console.log("Philosopher " + id + "took the right fork and started eating");

                        setTimeout(function () {
                            forks[f1].release();
                            console.log("Philosopher " + id + " has put down the left fork");
                            forks[f2].release();
                            console.log("Philosopher " + id + " has put down the right fork");
                            loopNaive(numberOfMeals - 1);
                        }, eatingTime);
                    })
                })
        };

    setTimeout(function () {
        console.log("Philosopher " + id + " is thinking");
        loopNaive(count);
    }, thinkTime);
}

Philosopher.prototype.startAsym = function (count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        thinkTime = 500,
        eatingTime = 500,

        swapIfPhilosopherIsEven = function () {
            if (id % 2 === 0)
                [f1, f2] = [f2, f1];
        },

        loopNaive = function (numberOfMeals) {
            var startTime = new Date().getTime();
            if (numberOfMeals > 0)
                forks[f1].acquire(function () {
                    console.log("Philosopher " + id + " took the first fork");
                    forks[f2].acquire(function () {
                        var stopTime = new Date().getTime();
                        console.log("Philosopher: #" + id + "# time: #" + (stopTime - startTime) + "#");
                        console.log("Philosopher " + id + "took the second fork and started eating");

                        setTimeout(function () {
                            forks[f1].release();
                            console.log("Philosopher " + id + " has put down the first fork");
                            forks[f2].release();
                            console.log("Philosopher " + id + " has put down the second fork");
                            loopNaive(numberOfMeals - 1);
                        }, eatingTime);
                    })
                })
        };

    setTimeout(function () {
        swapIfPhilosopherIsEven();
        console.log("Philosopher " + id + " is thinking");
        loopNaive(count);
    }, thinkTime);
}

var Referee = function (numberOfPhilosophers) {
    this.state = numberOfPhilosophers - 1;
    this.maxNumberOfIteration = 10;
    return this;
}

Referee.prototype.acquire = function (cb) {
    var maxTime = 1,
        getTime = iteration => {
            biggestThreshold = Math.pow(2, iteration);
            var randomTime = Math.floor(Math.random() * biggestThreshold);
            return randomTime;
        }

    var handler = i => {
        if (this.state > 0) {
            this.state--;
            return cb();
        }
        setTimeout(handler, getTime(i), Math.min(i + 1, this.maxNumberOfIteration));
    }
    setTimeout(handler, 1, 1);
}

Referee.prototype.release = function () {
    this.state++;
}



Philosopher.prototype.startConductor = function (count, referee) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        thinkTime = 500,
        eatingTime = 500,

        loopConductor = function (numberOfMeals) {
            var startTime = new Date().getTime();
            if (numberOfMeals > 0)
                referee.acquire(function () {
                    console.log("Philosopher " + id + " is granted to eat by referee");
                    forks[f1].acquire(function () {
                        console.log("Philosopher " + id + " took the left fork");
                        forks[f2].acquire(function () {
                            var stopTime = new Date().getTime();
                            console.log("Philosopher: #" + id + "# time: #" + (stopTime - startTime) + "#");
                            console.log("Philosopher " + id + "took the right fork and started eating");

                            setTimeout(function () {
                                forks[f1].release();
                                console.log("Philosopher " + id + " has put down the left fork");
                                forks[f2].release();
                                console.log("Philosopher " + id + " has put down the right fork");
                                referee.release();
                                console.log("Philosopher " + id + "  has told the referee he has stopped eating");
                                loopConductor(numberOfMeals - 1);
                            }, eatingTime);
                        })
                    })
                });
        };

    setTimeout(function () {
        console.log("Philosopher " + id + " is thinking");
        loopConductor(count);
    }, thinkTime);
}

    // TODO: wersja z jednoczesnym podnoszeniem widelców
    // Algorytm BEB powinien obejmować podnoszenie obu widelców, 
    // a nie każdego z osobna

    function acquireTwoForksAtOnce(fork1, fork2, cb) {
        var getTime = iteration => {
            biggestThreshold = Math.pow(2, iteration);
            var randomTime = Math.floor(Math.random() * biggestThreshold);
            // console.log(randomTime);
            return randomTime;
        };
    
        var handler = i => {
            if (fork1.state === 0 && fork2.state === 0) {
                fork1.state = 1;
                fork2.state = 1;
                return cb();
            }
            setTimeout(handler, getTime(i), Math.min(i + 1, fork1.maxNumberOfIteration));
        }
        setTimeout(handler, 1, 1);
    }

    Philosopher.prototype.startSimultanous = function (count) {
        var forks = this.forks,
            f1 = this.f1,
            f2 = this.f2,
            id = this.id,
            thinkTime = 500,
            eatingTime = 500,

            loopSimultanous = function (numberOfMeals) {
                var startTime = new Date().getTime();
                if (numberOfMeals > 0)
                    acquireTwoForksAtOnce(forks[f1], forks[f2], function () {
                        var stopTime = new Date().getTime();
                        console.log("Philosopher: #" + id + "# time: #" + (stopTime - startTime) + "#");
                        console.log("Philosopher " + id + " took two forks and started eating");
                        setTimeout(function () {
                            forks[f1].release();
                            console.log("Philosopher " + id + " has put down the left fork");
                            forks[f2].release();
                            console.log("Philosopher " + id + " has put down the right fork");
                            loopSimultanous(numberOfMeals - 1);
                        }, eatingTime)
                    })
            };

        setTimeout(function () {
            console.log("Philosopher " + id + " is thinking");
            loopSimultanous(count);
        }, thinkTime);

    }


    var N = 10;
    var forks = [];
    var philosophers = [];
    var referee = new Referee(N);
    for (var i = 0; i < N; i++) {
        forks.push(new Fork());
    }

    for (var i = 0; i < N; i++) {
        philosophers.push(new Philosopher(i, forks));
    }

    for (var i = 0; i < N; i++) {
        philosophers[i].startSimultanous(20);
    }