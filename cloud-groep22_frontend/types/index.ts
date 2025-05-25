export interface Exercise {
    id: number;
    name: string;
    type: string;
    rest: number;
    autoIncrease: boolean;
    autoIncreaseFactor: number;
    autoIncreaseWeightStep: number;
    autoIncreaseStartWeight: number;
    autoIncreaseMinSets: number;
    autoIncreaseMaxSets: number;
    autoIncreaseMinReps: number;
    autoIncreaseMaxReps: number;
    autoIncreaseStartDuration: number;
    autoIncreaseDurationSets: number;
    autoIncreaseCurrentSets: number;
    autoIncreaseCurrentReps: number;
    autoIncreaseCurrentDuration: number;
    autoIncreaseCurrentWeight: number;
    sets: Sets[];
    progressList: Progress[];
}

export interface Sets {
    id: number;
    reps: number;
    weight: number;
    duration: number;
}

export interface Progress {
    id: number;
    weight: number;
    duration: number;
    date: string;
}

export interface Bodyweight {
    date: string;
    bodyWeight: number;
}

export interface Workout {
    id: number;
    name: string;
    rest: number;
    exercises: Exercise[];
}