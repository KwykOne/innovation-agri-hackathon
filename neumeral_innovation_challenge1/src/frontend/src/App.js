import './App.css';

import SearchForm from './components/SearchForm';

function App() {
  return (
    <div className="">
      <header className="border-bottom bg-light p-4">
        <h2>कृषि इनपुट खोज</h2>
        <h4>Team Neumeral Technologies</h4>
        <p><i>for NABARD ONDC Hackathon</i></p>
      </header>

      <section>
        <SearchForm />
      </section>
    </div>
  );
}

export default App;
