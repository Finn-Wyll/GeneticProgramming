import React, { useState } from 'react';
import { Play } from 'lucide-react';

interface GPConfigFormProps {
  onTrain: (params: any) => void;
  loading: boolean;
}

export default function GPConfigForm({ onTrain, loading }: GPConfigFormProps) {
  const [params, setParams] = useState({
    modelType: 'arithmetic',
    populationSize: 200,
    maxGenerations: 100,
    crossoverRate: 0.8,
    mutationRate: 0.2,
    maxDepth: 5,
    mutationDepth: 3,
    tournamentSize: 5,
    seed: 42
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setParams(prev => ({
      ...prev,
      [name]: name === 'modelType' ? value : Number(value)
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onTrain(params);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2 space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Model Type</label>
          <select 
            name="modelType"
            value={params.modelType}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md bg-white text-sm text-gray-900 font-medium"
          >
            <option value="arithmetic">Arithmetic GP</option>
            <option value="decisionTree">Decision Tree GP</option>
          </select>
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Pop Size</label>
          <input 
            type="number" 
            name="populationSize"
            value={params.populationSize}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Generations</label>
          <input 
            type="number" 
            name="maxGenerations"
            value={params.maxGenerations}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Crossover %</label>
          <input 
            type="number" 
            step="0.1"
            name="crossoverRate"
            value={params.crossoverRate}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Mutation %</label>
          <input 
            type="number" 
            step="0.1"
            name="mutationRate"
            value={params.mutationRate}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Max Depth</label>
          <input 
            type="number" 
            name="maxDepth"
            value={params.maxDepth}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs font-bold text-gray-800 uppercase">Seed</label>
          <input 
            type="number" 
            name="seed"
            value={params.seed}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded-md text-sm text-gray-900 font-medium"
          />
        </div>
      </div>

      <button
        type="submit"
        disabled={loading}
        className={`w-full py-3 px-4 rounded-lg font-bold text-white transition-all flex items-center justify-center gap-2 ${
          loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-200'
        }`}
      >
        <Play className={`w-4 h-4 ${loading ? 'animate-pulse' : ''}`} />
        {loading ? 'Training...' : 'Train Model'}
      </button>
    </form>
  );
}
